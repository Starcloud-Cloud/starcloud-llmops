package com.starcloud.ops.business.app.service.image.impl;

import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageRespVO;
import com.starcloud.ops.business.app.domain.entity.ImageAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.image.ImageService;
import com.starcloud.ops.business.app.service.image.strategy.ImageHandlerHolder;
import com.starcloud.ops.business.app.service.image.strategy.handler.BaseImageHandler;
import com.starcloud.ops.business.app.service.upload.UploadImageRequest;
import com.starcloud.ops.business.app.service.upload.UploadService;
import com.starcloud.ops.business.app.util.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Resource
    private AppDictionaryService appDictionaryService;

    @Resource
    private ImageHandlerHolder imageHandlerHolder;

    @Resource
    private UploadService uploadService;

    /**
     * 获取图片元数据
     *
     * @return 图片元数据
     */
    @Override
    public Map<String, List<ImageMetaDTO>> meta() {
        Map<String, List<ImageMetaDTO>> meta = new HashMap<>(8);
        meta.put("model", ImageUtils.engineList());
        meta.put("upscalingModel", ImageUtils.upscalingEngineList());
        meta.put("samples", ImageUtils.samplesList());
        meta.put("imageSize", ImageUtils.imageSizeList());
        meta.put("sampler", ImageUtils.samplerList());
        meta.put("guidancePreset", ImageUtils.guidancePresetList());
        meta.put("stylePreset", ImageUtils.stylePresetList());
        meta.put("examplePrompt", appDictionaryService.examplePrompt());
        return meta;
    }

    /**
     * 上传图片
     *
     * @param image 上传图片
     * @return 图片信息
     */
    @Override
    public UploadImageInfoDTO upload(MultipartFile image) {
        try {
            UploadImageRequest request = new UploadImageRequest();
            request.setTenantId(TenantContextHolder.getTenantId());
            request.setName(image.getOriginalFilename());
            request.setPath("");
            request.setContent(IOUtils.toByteArray(image.getInputStream()));
            request.setLimitPixel(null);
            UploadImageInfoDTO imageInfo = uploadService.uploadImage(request);
            AppValidate.notNull(imageInfo, "上传图片失败！图片信息为空！");
            log.info("上传图片成功，图片信息为：{}", imageInfo);
            return imageInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 上传图片，图片大小不能超过1024 并且返回图片URL
     *
     * @param image 上传图片
     * @return 图片url
     */
    @Override
    public UploadImageInfoDTO uploadLimit1024(MultipartFile image) {
        try {
            UploadImageRequest request = new UploadImageRequest();
            request.setTenantId(TenantContextHolder.getTenantId());
            request.setName(image.getOriginalFilename());
            request.setPath("");
            request.setContent(IOUtils.toByteArray(image.getInputStream()));
            request.setLimitPixel(1024 * 1024);
            request.setLimitMessage("1024x1024");
            UploadImageInfoDTO imageInfo = uploadService.uploadImage(request);
            AppValidate.notNull(imageInfo, "上传图片失败！图片信息为空！");
            log.info("上传图片成功，图片信息为：{}", imageInfo);
            return imageInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 文本生成图片
     *
     * @param request 请求参数
     * @return 图片信息
     */
    @SuppressWarnings("all")
    @Override
    public ImageRespVO execute(ImageReqVO request) {
        // 获取SSE
        request.setSseEmitter(new SseEmitter(60000L));
        // 获取图片处理器
        BaseImageHandler handler = imageHandlerHolder.getHandler(request.getScene());
        request.setImageHandler(handler);
        // 构建 ImageAppEntity
        ImageAppEntity factory = AppFactory.factory(request);
        // 生成图片
        return factory.execute(request);
    }

}
