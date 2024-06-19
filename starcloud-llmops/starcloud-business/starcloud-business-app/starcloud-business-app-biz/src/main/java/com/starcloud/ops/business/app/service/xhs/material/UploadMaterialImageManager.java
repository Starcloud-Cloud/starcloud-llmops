package com.starcloud.ops.business.app.service.xhs.material;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.BlockPolicy;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.alibaba.fastjson.JSONObject;
import com.starcloud.ops.business.app.api.xhs.material.MaterialFieldConfigDTO;
import com.starcloud.ops.business.app.api.xhs.material.UploadMaterialImageDTO;
import com.starcloud.ops.business.app.api.xhs.material.dto.AbstractCreativeMaterialDTO;
import com.starcloud.ops.business.app.api.xhs.note.ImageInfo;
import com.starcloud.ops.business.app.api.xhs.note.NoteDetail;
import com.starcloud.ops.business.app.api.xhs.note.NoteImage;
import com.starcloud.ops.business.app.api.xhs.note.NoteTag;
import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request.ParseXhsReqVO;
import com.starcloud.ops.business.app.enums.xhs.XhsDetailConstants;
import com.starcloud.ops.business.app.service.xhs.crawler.impl.XhsDumpServiceImpl;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.framework.common.api.util.StringUtil;
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
import java.nio.file.Files;
import java.nio.file.Paths;
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

    @Resource(name = "xhsDumpServiceImpl")
    private XhsDumpServiceImpl xhsNoteDetailWrapper;

    private ThreadPoolExecutor threadPoolExecutor;

    // 导入任务阻塞队列
    private final BlockingQueue<UploadMaterialImageDTO> blockingQueue = new ArrayBlockingQueue<>(8);

    @Override
    public void afterPropertiesSet() {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        threadPoolExecutor = new ThreadPoolExecutor(4, 16,
                1, TimeUnit.MINUTES, new SynchronousQueue<>(), r -> {
            Thread thread = new Thread(r, "image-upload-" + atomicInteger.getAndIncrement());
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
                parse(uploadMaterialDTO);
            } catch (Exception e) {
                log.warn("start upload error", e);
            }
        }
    }


    private void parse(UploadMaterialImageDTO uploadMaterialDTO) {
        long start = System.currentTimeMillis();
        String parseUid = uploadMaterialDTO.getParseUid();
        List<Map<String, Object>> materialList = uploadMaterialDTO.getMaterialList();
        List<MaterialFieldConfigDTO> imageField = uploadMaterialDTO.getImageField();
        Long tenantId = uploadMaterialDTO.getTenantId();
        String unzipDir = uploadMaterialDTO.getUnzipDir();
        if (!uploadMaterialDTO.containsImage()) {
            return;
        }
        boolean containsDocument = uploadMaterialDTO.isContainsDocument();
        String documentFieldName = uploadMaterialDTO.getDocumentFieldName();
        CountDownLatch countDownLatch = new CountDownLatch(materialList.size());
        log.info("start upload material image, parseUid = {}, size={}", parseUid, materialList.size());
        try {
            for (Map<String, Object> material : materialList) {
                threadPoolExecutor.execute(() -> {
                    try {
                        TenantContextHolder.setIgnore(false);
                        TenantContextHolder.setTenantId(tenantId);
                        extracted(material, containsDocument,
                                documentFieldName, parseUid, unzipDir, imageField);
                    } catch (Exception e) {
                        log.warn("extracted image error,parseUid={},material={}", parseUid, JSONUtil.toJsonPrettyStr(material), e);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            // wait
            if (countDownLatch.await(120, TimeUnit.SECONDS)) {
                long end = System.currentTimeMillis();
                redisTemplate.boundValueOps(MATERIAL_PREFIX + parseUid).set(JsonUtils.toJsonString(materialList), 3, TimeUnit.DAYS);
                log.info("upload word image success, parseUid={}, {} ms", parseUid, end - start);
            } else {
                // timeout
                redisTemplate.boundValueOps(MATERIAL_IMPORT_ERROR + parseUid).set("超时请重试", 3, TimeUnit.DAYS);
                log.warn("upload word image timeout");
            }
        } catch (Exception e) {
            log.warn("upload word image error", e);
            redisTemplate.boundValueOps(MATERIAL_IMPORT_ERROR + parseUid).set(e.getMessage(), 3, TimeUnit.DAYS);
        }
    }

    private void extracted(Map<String, Object> material, boolean containsDocument, String documentFieldName,
                           String parseUid, String unzipDir, List<MaterialFieldConfigDTO> imageField) {

        if (containsDocument) {
            String documentPath = String.valueOf(material.get(documentFieldName));
            Iterator<String> documentScreenshot = documentScreenshot(parseUid, documentPath, unzipDir).iterator();
            for (MaterialFieldConfigDTO materialFieldConfigDTO : imageField) {
                String imagePath = (String) material.get(materialFieldConfigDTO.getFieldName());
                String ossUrl = localeToOss(imagePath, parseUid, unzipDir);
                if (StringUtils.isBlank(ossUrl) && documentScreenshot.hasNext()) {
                    ossUrl = documentScreenshot.next();
                }
                material.put(materialFieldConfigDTO.getFieldName(), ossUrl);
            }
        } else {
            // 不需要截图文档
            for (MaterialFieldConfigDTO materialFieldConfigDTO : imageField) {
                String imagePath = (String) material.get(materialFieldConfigDTO.getFieldName());
                String ossUrl = localeToOss(imagePath, parseUid, unzipDir);
                material.put(materialFieldConfigDTO.getFieldName(), ossUrl);
            }
        }
    }

    private String localeToOss(String imagePath, String parseUid, String unzipDir) {
        String ossUrl = StringUtils.EMPTY;
        if (StringUtils.isBlank(imagePath)) {
            return ossUrl;
        } else if (StringUtil.isPath(imagePath)) {
            // 本地图片存在使用本地图片
            ossUrl = localImageUpload(unzipDir, imagePath, parseUid);
        } else if (StringUtil.isUrl(imagePath)) {
            // excel 中有内容 为http图片地址
            String relativePath = "material" + File.separator + parseUid;
            ossUrl = ImageUploadUtils.dumpToOss(imagePath, IdUtil.fastSimpleUUID(), relativePath);
        }
        return ossUrl;
    }

    private List<String> documentScreenshot(String parseUid, String documentPath, String unzipDir) {
        HashMap<String, Object> paramMap = new HashMap<>();
        File document = Paths.get(unzipDir, documentPath).toFile();
        if (!document.exists()) {
            return Collections.emptyList();
        }
        paramMap.put("file", document);
        paramMap.put("parseUid", parseUid);
        String result = HttpUtil.post(WORD_PARSE, paramMap);
        List<String> documentScreenshot = JSONUtil.parseArray(result).toList(String.class);
        if (documentScreenshot == null) {
            documentScreenshot = Collections.emptyList();
        }
        return documentScreenshot;
    }


    private String localImageUpload(String unzipDir, String imagePath, String parseUid) {
        File file = Paths.get(unzipDir,"images", imagePath).toFile();
        String ossUrl = StringUtils.EMPTY;
        if (!file.exists()) {
            return ossUrl;
        }
        try {
            BufferedImage read = ImageIO.read(file);
            if (Objects.isNull(read)) {
                log.warn("Image type of file is not supported! unzipDir={}, imagePath={}", unzipDir, imagePath);
                return StringUtils.EMPTY;
            }

            String relativePath = "material" + File.separator + parseUid;
            return ImageUploadUtils.uploadImage(file.getName(), relativePath, IoUtil.readBytes(Files.newInputStream(file.toPath()))).getUrl();

        } catch (Exception e) {
            log.warn("Image is not exist unzipDir={}, imagePath={}", unzipDir, imagePath);
            return ossUrl;
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
