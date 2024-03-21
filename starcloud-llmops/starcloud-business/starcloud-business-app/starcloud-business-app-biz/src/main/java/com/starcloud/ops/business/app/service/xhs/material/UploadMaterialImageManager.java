package com.starcloud.ops.business.app.service.xhs.material;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.BlockPolicy;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import com.starcloud.ops.business.app.api.xhs.material.UploadMaterialImageDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractBaseCreativeMaterialDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
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
                distribute(uploadMaterialDTO);
            } catch (Exception e) {
                log.warn("start upload error", e);
            }
        }
    }

    // 上传图片
    private void distribute(UploadMaterialImageDTO uploadMaterialDTO) {
        String parseUid = uploadMaterialDTO.getParseUid();

        List<? extends AbstractBaseCreativeMaterialDTO> materialDTOList = uploadMaterialDTO.getMaterialDTOList();
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
            for (AbstractBaseCreativeMaterialDTO materialDTO : materialDTOList) {
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
    private void upload(AbstractBaseCreativeMaterialDTO materialDTO,
                        Field field,
                        String parseUid,
                        CountDownLatch countDownLatch) {
        try {
            field.setAccessible(true);
            String imageName = (String) field.get(materialDTO);

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
