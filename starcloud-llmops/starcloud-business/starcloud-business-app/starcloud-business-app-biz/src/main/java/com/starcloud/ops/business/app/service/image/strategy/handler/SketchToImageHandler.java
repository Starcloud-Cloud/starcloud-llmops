package com.starcloud.ops.business.app.service.image.strategy.handler;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.SketchToImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.SketchToImageResponse;
import com.starcloud.ops.business.app.convert.image.ImageConvert;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.feign.request.clipdrop.SketchToImageClipDropRequest;
import com.starcloud.ops.business.app.feign.response.ClipDropImage;
import com.starcloud.ops.business.app.service.image.clipdrop.ClipDropImageService;
import com.starcloud.ops.business.app.service.image.strategy.ImageScene;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-25
 */
@Slf4j
@Component
@ImageScene(AppSceneEnum.IMAGE_SKETCH)
public class SketchToImageHandler extends BaseImageHandler<SketchToImageRequest, SketchToImageResponse> {

    @Resource
    private ClipDropImageService clipDropImageService;

    /**
     * 构建图片配置信息配置
     *
     * @param request 请求
     */
    @Override
    public void handleRequest(SketchToImageRequest request) {
        log.info("SketchToImageHandler handleRequest: 草图生成图片不需要处理");
    }

    /**
     * 处理图片
     *
     * @param request 图片请求
     * @return 图片响应
     */
    @Override
    public SketchToImageResponse handle(SketchToImageRequest request) {
        log.info("SketchToImageHandler handle: 草图生成图片开始");
        // 上传草图
        byte[] imageBytes = ImageUtils.handlerBase64Image(request.getSketchImage()).getBytes(StandardCharsets.UTF_8);
        String uuid = IdUtil.fastSimpleUUID();
        String url = ImageUploadUtils.upload(uuid, MediaType.IMAGE_PNG_VALUE, ImageUploadUtils.GENERATE, imageBytes);

        SketchToImageClipDropRequest sketchToImageClipDropRequest = new SketchToImageClipDropRequest();
        sketchToImageClipDropRequest.setPrompt(request.getPrompt());
        sketchToImageClipDropRequest.setSketchFile(ImageUploadUtils.getImageFile(imageBytes));

        // 调用草图生成图片接口
        ClipDropImage clipDropImage = clipDropImageService.sketchToImage(sketchToImageClipDropRequest);
        if (Objects.isNull(clipDropImage)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_FAIL, "草图生成图片失败");
        }
        SketchToImageResponse response = new SketchToImageResponse();
        response.setOriginalUrl(url);
        response.setPrompt(request.getPrompt());
        ImageDTO image = ImageConvert.INSTANCE.convert(clipDropImage);
        response.setImages(Collections.singletonList(image));
        log.info("SketchToImageHandler handle: 草图生成图片结束：{}", JSONUtil.toJsonStr(response));
        return response;
    }

    /**
     * 处理日志消息
     *
     * @param messageRequest 日志信息
     * @param request        请求
     * @param response       响应
     */
    @Override
    public void handleLogMessage(LogAppMessageCreateReqVO messageRequest, SketchToImageRequest request, SketchToImageResponse response) {
        log.info("SketchToImageHandler handleLogMessage: 草图生成图片处理日志消息开始...");
        messageRequest.setMessage(request.getPrompt());
        log.info("SketchToImageHandler handleLogMessage: 草图生成图片处理日志消息结束...");
    }
}
