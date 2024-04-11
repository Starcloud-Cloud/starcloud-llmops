package com.starcloud.ops.business.app.service.xhs.material;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.BlockPolicy;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.starcloud.ops.business.app.api.xhs.material.UploadMaterialImageDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.UPLOAD_QUEUE_FULL;
import static com.starcloud.ops.business.app.enums.xhs.CreativeConstants.*;

@Slf4j
@Component
public class UploadMaterialImageManager implements InitializingBean {

    @Resource
    private FileService fileService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 图片上传线程池
    private ThreadPoolExecutor threadPoolExecutor;

    // 导入任务阻塞队列
    private final BlockingQueue<UploadMaterialImageDTO> blockingQueue = new ArrayBlockingQueue<>(8);

    @Override
    public void afterPropertiesSet() {
        threadPoolExecutor = new ThreadPoolExecutor(4, 16,
                1, TimeUnit.MINUTES, new SynchronousQueue<>(), r -> {
            Thread thread = new Thread(r, "image-upload-thread");
            thread.setDaemon(true);
            return thread;
        }, new BlockPolicy());

        Thread thread = new Thread(this::start, "material-upload");
        thread.setDaemon(true);
        thread.start();
    }

    public void submit(UploadMaterialImageDTO uploadMaterialDTO) {
        if (!blockingQueue.offer(uploadMaterialDTO)) {
            // 队列已满，重新上传
            throw exception(UPLOAD_QUEUE_FULL);
        }
    }

    // 启动任务
    private void start() {
        while (true) {
            try {
                UploadMaterialImageDTO uploadMaterialDTO = blockingQueue.take();
                if (MaterialTypeEnum.CONTRACT.getCode().equals(uploadMaterialDTO.getMaterialType())) {
                    parseWord(uploadMaterialDTO);
                } else {
                    parse(uploadMaterialDTO);
                }
            } catch (Exception e) {
                log.warn("start upload error", e);
            }
        }
    }

    // 处理 word 图片
    private void parseWord(UploadMaterialImageDTO uploadMaterialDTO) {
        // 判断type 类型
        String parseUid = uploadMaterialDTO.getParseUid();

        List<? extends AbstractCreativeMaterialDTO> materialDTOList = uploadMaterialDTO.getMaterialDTOList();
        List<Field> imageField = uploadMaterialDTO.getImageField();
        int subCount = materialDTOList.size() * imageField.size();
        CountDownLatch countDownLatch = new CountDownLatch(subCount);
        log.info("start upload word material image, parseUid = {}, size={}", parseUid, subCount);
        long start = System.currentTimeMillis();
        try {
            for (int i = 0; i < materialDTOList.size(); i++) {
                AbstractCreativeMaterialDTO materialDTO = materialDTOList.get(i);
                String wordPath = TMP_DIR_PATH + File.separator + parseUid + File.separator
                        + materialDTO.getType() + File.separator + "word" + i + ".docx";

                File word = new File(wordPath);
                if (!word.exists()) {
                    for (int j = 0; j < imageField.size(); j++) {
                        countDownLatch.countDown();
                    }
                    continue;
                }

                Document doc = new Document(Files.newInputStream(word.toPath()));

                String localPath = TMP_DIR_PATH + File.separator + parseUid + File.separator
                        + materialDTO.getType() + File.separator + "wordImages" + File.separator;

                int pageCount = doc.getPageCount();
                for (int j = 0; j < imageField.size(); j++) {
                    if (j >= pageCount) {
                        // 字段数 大于word页数
                        countDownLatch.countDown();
                        continue;
                    }
                    Document extractedPage = doc.extractPages(j, 1);
                    Field field = imageField.get(j);
                    String imageName = FileUtil.getPrefix(word) + "-" + j + ".png";
                    String ossPath = "material" + File.separator + parseUid
                            + File.separator + imageName;
                    threadPoolExecutor.execute(() ->
                            upload(extractedPage, materialDTO, field,
                                    localPath, ossPath, imageName, countDownLatch));
                }
            }
            if (countDownLatch.await(1, TimeUnit.MINUTES)) {
                long end = System.currentTimeMillis();
                redisTemplate.boundValueOps(MATERIAL_PREFIX + parseUid).set(JsonUtils.toJsonString(materialDTOList), 3, TimeUnit.DAYS);
                log.info("upload word image success, {} ms", end - start);
            } else {
                redisTemplate.boundValueOps(MATERIAL_IMPORT_ERROR + parseUid).set("超时请重试", 3, TimeUnit.DAYS);
                log.warn("upload wod image timeout");
            }
        } catch (Exception e) {
            log.warn("upload word image error", e);
            redisTemplate.boundValueOps(MATERIAL_IMPORT_ERROR + parseUid).set(e.getMessage(), 3, TimeUnit.DAYS);
        }

    }

    private void upload(Document extractedPage, AbstractCreativeMaterialDTO materialDTO,
                        Field field, String localPath, String ossPath, String imageName, CountDownLatch countDownLatch) {
        try {
            field.setAccessible(true);
            String filePath = localPath + File.separator + imageName;
            extractedPage.save(filePath, SaveFormat.PNG);
            String url = fileService.createFile(imageName, ossPath, IoUtil.readBytes(Files.newInputStream(Paths.get(filePath))));
            field.set(materialDTO, url);
        } catch (Exception e) {
            log.warn("word to image error", e);
        } finally {
            countDownLatch.countDown();
        }
    }


    // 上传图片
    private void parse(UploadMaterialImageDTO uploadMaterialDTO) {
        String parseUid = uploadMaterialDTO.getParseUid();

        List<? extends AbstractCreativeMaterialDTO> materialDTOList = uploadMaterialDTO.getMaterialDTOList();
        List<Field> imageField = uploadMaterialDTO.getImageField();
        if (!uploadMaterialDTO.containsImage()) {
            return;
        }
        // 子任务数量
        int subCount = materialDTOList.size() * imageField.size();
        log.info("start upload material image, parseUid = {}, size={}", parseUid, subCount);
        CountDownLatch countDownLatch = new CountDownLatch(subCount);
        try {
            long start = System.currentTimeMillis();
            for (AbstractCreativeMaterialDTO materialDTO : materialDTOList) {
                for (Field field : imageField) {
                    threadPoolExecutor.execute(() -> upload(materialDTO, field,
                            parseUid, countDownLatch));
                }
            }
            if (countDownLatch.await(120, TimeUnit.SECONDS)) {
                long end = System.currentTimeMillis();
                redisTemplate.boundValueOps(MATERIAL_PREFIX + parseUid).set(JsonUtils.toJsonString(materialDTOList), 3, TimeUnit.DAYS);
                log.info("upload image success, {} ms", end - start);
            } else {
                // 失败
                redisTemplate.boundValueOps(MATERIAL_IMPORT_ERROR + parseUid).set("超时请重试", 3, TimeUnit.DAYS);
                log.warn("upload image timeout");
            }
        } catch (Exception e) {
            log.warn("upload image error", e);
            redisTemplate.boundValueOps(MATERIAL_IMPORT_ERROR + parseUid).set(e.getMessage(), 3, TimeUnit.DAYS);
        }
    }

    // 上传 更新地址
    private void upload(AbstractCreativeMaterialDTO materialDTO,
                        Field field,
                        String parseUid,
                        CountDownLatch countDownLatch) {
        try {
            field.setAccessible(true);
            // 图片链接或相对路径
            String imageName = (String) field.get(materialDTO);

            // 判断是不是图片链接  是图片链接跳过
            InputStream inputStream = null;
            try {
                URL url = new URL(imageName);
                URLConnection urlConnection = url.openConnection();
                urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
                inputStream = urlConnection.getInputStream();

                BufferedImage read = ImageIO.read(inputStream);
                if (Objects.nonNull(read)) {
                    return;
                }
            } catch (MalformedURLException ignored) {
                log.warn("ignored MalformedURLException");
            } catch (Exception e) {
                log.warn("image user error, parseUid={}, imageName={}", parseUid, imageName, e);
                field.set(materialDTO, StringUtils.EMPTY);
            } finally {
                if (Objects.nonNull(inputStream)) {
                    inputStream.close();
                }
            }

            // 系统默认临时文件目录/material/{parseUid}/{materialType}/images/{imageName}
            String localPath = TMP_DIR_PATH + File.separator + parseUid + File.separator
                    + materialDTO.getType() + File.separator + "images" + File.separator
                    + imageName;

            File file = new File(localPath);
            if (file.exists()) {
                // 上传&更新图片地址
                BufferedImage read = ImageIO.read(file);
                if (Objects.isNull(read)) {
                    log.warn("Image type of file is not supported! parseUid={}, imageName={}", parseUid, imageName);
                    return;
                }

                String relativePath = "material" + File.separator + parseUid + File.separator + imageName;
                String url = fileService.createFile(imageName, relativePath, IoUtil.readBytes(Files.newInputStream(file.toPath())));
                field.set(materialDTO, url);
            } else {
                log.warn("Image is not exist parseUid={}, imageName={}", parseUid, imageName);
                field.set(materialDTO, StringUtils.EMPTY);
            }
        } catch (Exception e) {
            log.warn("upload error:", e);
        } finally {
            countDownLatch.countDown();
        }
    }

}
