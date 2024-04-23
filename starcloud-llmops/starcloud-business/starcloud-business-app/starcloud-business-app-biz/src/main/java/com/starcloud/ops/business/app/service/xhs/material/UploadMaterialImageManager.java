package com.starcloud.ops.business.app.service.xhs.material;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.BlockPolicy;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.xhs.material.UploadMaterialImageDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.ContractCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.note.ImageInfo;
import com.starcloud.ops.business.app.api.xhs.note.NoteDetail;
import com.starcloud.ops.business.app.api.xhs.note.NoteImage;
import com.starcloud.ops.business.app.api.xhs.note.NoteTag;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.ParseXhsReqVO;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;
import com.starcloud.ops.business.app.service.xhs.crawler.XhsNoteDetailWrapper;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.app.enums.CreativeErrorCodeConstants.UPLOAD_QUEUE_FULL;
import static com.starcloud.ops.business.app.enums.xhs.CreativeConstants.*;

@Slf4j
@Component
public class UploadMaterialImageManager implements InitializingBean {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private XhsNoteDetailWrapper xhsNoteDetailWrapper;


    // 图片上传线程池
    private ThreadPoolExecutor threadPoolExecutor;

    // 导入任务阻塞队列
    private final BlockingQueue<UploadMaterialImageDTO> blockingQueue = new ArrayBlockingQueue<>(8);

    @Override
    public void afterPropertiesSet() {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        threadPoolExecutor = new ThreadPoolExecutor(4, 16,
                1, TimeUnit.MINUTES, new SynchronousQueue<>(), r -> {
            Thread thread = new Thread(r, "image-upload-thread-" + atomicInteger.getAndIncrement());
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

    /**
     * 启动任务
     */
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
        int subCount = materialDTOList.size();
        log.info("start upload word material image, parseUid = {}, size={}", parseUid, subCount);
        long start = System.currentTimeMillis();
        try {
            Map<Integer, Future<List<String>>> parseFuture = new HashMap<>(materialDTOList.size());
            for (int i = 0; i < materialDTOList.size(); i++) {
                ContractCreativeMaterialDTO materialDTO = (ContractCreativeMaterialDTO) materialDTOList.get(i);
                if (StringUtils.isBlank(materialDTO.getDocRelativeAddr())) {
                    continue;
                }

                String wordPath = TMP_DIR_PATH + File.separator + parseUid + File.separator
                        + materialDTO.getType() + File.separator + materialDTO.getDocRelativeAddr();
                File word = new File(wordPath);
                if (!word.exists()) {
                    continue;
                }
                Future<List<String>> future = threadPoolExecutor.submit(() -> upload(word, parseUid));
                parseFuture.put(i, future);
            }

            // 填充图片地址
            for (int i = 0; i < materialDTOList.size() && parseFuture.containsKey(i); i++) {
                List<String> rowImages;
                try {
                    rowImages = parseFuture.get(i).get(20, TimeUnit.SECONDS);
                } catch (Exception e) {
                    log.warn("wait error", e);
                    continue;
                }

                AbstractCreativeMaterialDTO materialDTO = materialDTOList.get(i);
                for (int j = 0; j < imageField.size(); j++) {
                    Field field = imageField.get(j);
                    field.setAccessible(true);

                    String imgAddr = (String) field.get(materialDTO);
                    String url = StringUtils.EMPTY;
                    if (j < rowImages.size()) {
                        url = rowImages.get(j);
                    }

                    // excel 中没有内容 使用截图图片
                    if (StringUtils.isBlank(imgAddr)) {
                        field.set(materialDTO, url);
                        continue;
                    }
                    // excel 中有内容 为http图片地址
                    if (isImage(imgAddr)) {
                        continue;
                    }
                    // 本地图片存在使用本地图片
                    String localImage = localImage(imgAddr, parseUid, materialDTO.getType());
                    if (!StringUtils.isBlank(localImage)) {
                        url = localImage;
                    }
                    field.set(materialDTO, url);
                }
            }
            long end = System.currentTimeMillis();
            redisTemplate.boundValueOps(MATERIAL_PREFIX + parseUid).set(JsonUtils.toJsonString(materialDTOList), 3, TimeUnit.DAYS);
            log.info("upload word image success, {} ms", end - start);
        } catch (Exception e) {
            log.warn("upload word image error", e);
            redisTemplate.boundValueOps(MATERIAL_IMPORT_ERROR + parseUid).set(e.getMessage(), 3, TimeUnit.DAYS);
        }

    }

    private List<String> upload(File word, String parseUid) {
        try {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("file", word);
            paramMap.put("parseUid", parseUid);
            String result = HttpUtil.post(WORD_PARSE, paramMap);
            return JSONUtil.parseArray(result).toList(String.class);
        } catch (Exception e) {
            log.warn("word to image error", e);
            return Collections.emptyList();
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

            // 判断是图片链接跳过
            if (isImage(imageName)) {
                return;
            }

            String url = localImage(imageName, parseUid, materialDTO.getType());
            field.set(materialDTO, url);
        } catch (Exception e) {
            log.warn("upload error:", e);
        } finally {
            countDownLatch.countDown();
        }
    }


    private boolean isImage(String addr) throws Exception {
        InputStream inputStream = null;
        try {
            URL url = new URL(addr);
            URLConnection urlConnection = url.openConnection();
            urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
            inputStream = urlConnection.getInputStream();

            BufferedImage read = ImageIO.read(inputStream);
            if (Objects.nonNull(read)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            if (Objects.nonNull(inputStream)) {
                inputStream.close();
            }
        }
        return false;
    }


    private String localImage(String addr, String parseUid, String materialType) throws Exception {
        // 系统默认临时文件目录/material/{parseUid}/{materialType}/images/{imageName}
        String localPath = TMP_DIR_PATH + File.separator + parseUid + File.separator
                + materialType + File.separator + "images" + File.separator
                + addr;

        File file = new File(localPath);
        if (file.exists()) {
            // 上传&更新图片地址
            BufferedImage read = ImageIO.read(file);
            if (Objects.isNull(read)) {
                log.warn("Image type of file is not supported! parseUid={}, imageName={}", parseUid, addr);
                return StringUtils.EMPTY;
            }

            String relativePath = "material" + File.separator + parseUid + File.separator + addr;

            return ImageUploadUtils.uploadImage(addr,relativePath,IoUtil.readBytes(Files.newInputStream(file.toPath()))).getUrl();
        } else {
            log.warn("Image is not exist parseUid={}, imageName={}", parseUid, addr);
            return StringUtils.EMPTY;
        }

    }

    public List<AbstractCreativeMaterialDTO> parseXhs(ParseXhsReqVO parseXhsReqVO) {
        long start = System.currentTimeMillis();
        log.info("start parse xhs note");
        List<String> noteUrlList = parseXhsReqVO.getNoteUrlList();
        Map<String, AbstractCreativeMaterialDTO> resultMap = new ConcurrentHashMap<>(noteUrlList.size());
        CountDownLatch countDownLatch = new CountDownLatch(noteUrlList.size());

        for (String noteUrl : noteUrlList) {
            threadPoolExecutor.execute(() -> parseXhs(noteUrl, parseXhsReqVO.getMaterialType(), resultMap, countDownLatch));
        }

        try {
            if (countDownLatch.await(30, TimeUnit.SECONDS)) {
                long end = System.currentTimeMillis();
                log.info("parse xhs note success, {} ms", end - start);
            }
            return noteUrlList.stream().map(resultMap::get).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("xhs note wait error", e);
        }
        return Collections.emptyList();
    }

    private void parseXhs(String noteUrl, String materialType, Map<String, AbstractCreativeMaterialDTO> resultMap, CountDownLatch countDownLatch) {
        try {
            XhsDetailConstants.validNoteUrl(noteUrl);
            String noteId = XhsDetailConstants.parsingNoteId(noteUrl);
            NoteDetail noteDetail = xhsNoteDetailWrapper.requestDetail(noteId).getNoteDetail();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", materialType);
            jsonObject.put("title", noteDetail.getTitle());
            jsonObject.put("content", noteDetail.getDesc());

            List<String> images = new ArrayList<>();
            if (!CollectionUtils.isEmpty(noteDetail.getImageList())) {
                for (NoteImage noteImage : noteDetail.getImageList()) {
                    List<String> imageList = Optional.ofNullable(noteImage.getInfoList()).orElse(Collections.emptyList()).stream().filter(info -> "WB_DFT".equalsIgnoreCase(info.getImageScene())).map(ImageInfo::getUrl).collect(Collectors.toList());
                    images.addAll(imageList);
                }
                jsonObject.put("images", images);
            }

            if (!CollectionUtils.isEmpty(noteDetail.getTagList())) {
                jsonObject.put("tags", noteDetail.getTagList().stream().map(NoteTag::getName).collect(Collectors.toList()));
            }
            jsonObject.put("link", noteUrl);
            resultMap.put(noteUrl, JsonUtils.parseObject(jsonObject.toJSONString(), AbstractCreativeMaterialDTO.class));
        } catch (Exception e) {
            log.warn("parse xhs note error, noteUrl={}", noteUrl, e);
        }
        countDownLatch.countDown();
    }
}
