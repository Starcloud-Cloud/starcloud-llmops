package com.starcloud.ops.business.app.service.image.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
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
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Base64;
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

    /**
     * 图片处理引擎
     */
    private static final String ENGINE = "clip-drop-sketch-to-image";

    @Resource
    private ClipDropImageService clipDropImageService;

    /**
     * 获取图片处理引擎
     *
     * @param request 请求
     */
    @Override
    public String obtainEngine(SketchToImageRequest request) {
        return ENGINE;
    }

    /**
     * 构建图片配置信息配置
     *
     * @param request 请求
     */
    @Override
    public void handleRequest(SketchToImageRequest request) {
        log.info("草图生成图片：请求参数：{}", JSONUtil.toJsonStr(request));
    }

    /**
     * 处理图片
     *
     * @param request 图片请求
     * @return 图片响应
     */
    @Override
    public SketchToImageResponse handleImage(SketchToImageRequest request) {
        log.info("草图生成图片开始...");
        // 上传草图
        byte[] imageBytes = Base64.getDecoder().decode(ImageUtils.handlerBase64Image(request.getSketchImage()));
        String uuid = IdUtil.fastSimpleUUID();
        String url = ImageUploadUtils.upload(uuid, MediaType.IMAGE_PNG_VALUE, ImageUploadUtils.UPLOAD, imageBytes);

        SketchToImageClipDropRequest sketchToImageClipDropRequest = new SketchToImageClipDropRequest();
        sketchToImageClipDropRequest.setPrompt(request.getPrompt());
        sketchToImageClipDropRequest.setSketchFile(ImageUploadUtils.getImageFile(imageBytes));

        // 调用草图生成图片接口
        ClipDropImage clipDropImage = clipDropImageService.sketchToImage(sketchToImageClipDropRequest);
        AppValidate.notNull(clipDropImage, ErrorCodeConstants.GENERATE_IMAGE_EMPTY);

        // 构建响应
        SketchToImageResponse response = new SketchToImageResponse();
        response.setOriginalUrl(url);
        response.setPrompt(request.getPrompt());
        ImageDTO image = ImageConvert.INSTANCE.convert(clipDropImage);
        response.setImages(Collections.singletonList(image));

        log.info("草图生成图片结束：{}", JSONUtil.toJsonStr(response));
        return response;
    }

    /**
     * 获取图片处理的积分
     *
     * @param request  请求
     * @param response 响应
     * @return 积分
     */
    @Override
    public Integer getCostPoints(SketchToImageRequest request, SketchToImageResponse response) {
        return 6;
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
        messageRequest.setAnswerUnitPrice(ImageUtils.CD_PRICE);
        if (Objects.nonNull(response) && CollectionUtil.isNotEmpty(response.getImages())) {
            messageRequest.setTotalPrice(new BigDecimal("1.6").multiply(ImageUtils.CD_PRICE));
        }
        messageRequest.setMessage(request.getPrompt());
    }
}
