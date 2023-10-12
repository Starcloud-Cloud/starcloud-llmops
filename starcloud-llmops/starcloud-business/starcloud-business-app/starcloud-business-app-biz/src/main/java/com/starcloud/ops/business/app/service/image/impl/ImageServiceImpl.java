package com.starcloud.ops.business.app.service.image.impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.api.image.vo.query.HistoryImageRecordsQuery;
import com.starcloud.ops.business.app.api.log.vo.response.ImageLogMessageRespVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageRespVO;
import com.starcloud.ops.business.app.domain.entity.ImageAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.dict.AppDictionaryService;
import com.starcloud.ops.business.app.service.image.ImageService;
import com.starcloud.ops.business.app.service.image.strategy.ImageHandlerHolder;
import com.starcloud.ops.business.app.service.image.strategy.handler.BaseImageHandler;
import com.starcloud.ops.business.app.service.log.impl.AppLogServiceImpl;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessagePageReqVO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
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
    private AppLogServiceImpl appLogService;

    @Resource
    private AppDictionaryService appDictionaryService;

    @Resource
    private ImageHandlerHolder imageHandlerHolder;

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
     * 查询历史图片列表
     *
     * @return 图片列表
     */
    @Override
    public PageResult<ImageLogMessageRespVO> historyImageRecords(HistoryImageRecordsQuery query) {
        // 查询日志消息记录
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        AppLogMessagePageReqVO messageQuery = new AppLogMessagePageReqVO();
        messageQuery.setAppMode(AppModelEnum.IMAGE.name());
        messageQuery.setFromScene(query.getScene());
        messageQuery.setCreator(String.valueOf(loginUserId));
        if (AppSceneEnum.WEB_IMAGE.name().equals(query.getScene())) {
            messageQuery.setStatus(LogStatusEnum.SUCCESS.name());
        } else {
            if (StringUtils.isNotEmpty(query.getStatus())) {
                messageQuery.setStatus(query.getStatus());
            }
        }
        return appLogService.pageImageAppLogMessage(messageQuery);
    }

    /**
     * 上传图片
     *
     * @param image 上传图片
     * @return 图片信息
     */
    @Override
    public UploadImageInfoDTO upload(MultipartFile image) {
        log.info("开始上传图片，ContentType: {}, imageName: {}", image.getContentType(), image.getOriginalFilename());
        return ImageUploadUtils.uploadImage(image, ImageUploadUtils.UPLOAD);
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
