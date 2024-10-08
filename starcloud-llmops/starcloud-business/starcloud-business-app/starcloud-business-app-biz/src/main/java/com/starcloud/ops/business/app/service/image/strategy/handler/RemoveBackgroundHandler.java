package com.starcloud.ops.business.app.service.image.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.RemoveBackgroundRequest;
import com.starcloud.ops.business.app.api.image.vo.response.RemoveBackgroundResponse;
import com.starcloud.ops.business.app.convert.image.ImageConvert;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.feign.dto.ClipDropImage;
import com.starcloud.ops.business.app.feign.request.clipdrop.ImageFileClipDropRequest;
import com.starcloud.ops.business.app.service.image.clipdrop.ClipDropImageService;
import com.starcloud.ops.business.app.service.image.strategy.ImageScene;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-25
 */
@Slf4j
@Component
@ImageScene(AppSceneEnum.IMAGE_REMOVE_BACKGROUND)
public class RemoveBackgroundHandler extends BaseImageHandler<RemoveBackgroundRequest, RemoveBackgroundResponse> {

    /**
     * 图片处理引擎
     */
    private static final String ENGINE = "clip-drop-remove-background";

    @Resource
    private ClipDropImageService clipDropImageService;

    /**
     * 获取图片处理引擎
     *
     * @param request 请求
     */
    @Override
    public String obtainEngine(RemoveBackgroundRequest request) {
        return ENGINE;
    }

    /**
     * 构建图片配置信息配置
     *
     * @param request 请求
     */
    @Override
    public void handleRequest(RemoveBackgroundRequest request) {

    }

    /**
     * 处理图片
     *
     * @param request 图片请求
     * @return 图片响应
     */
    @Override
    public RemoveBackgroundResponse handleImage(RemoveBackgroundRequest request) {
        log.info("去除图片背景【智能抠图】开始...");
        // 处理请求参数
        ImageFileClipDropRequest imageFileClipDropRequest = new ImageFileClipDropRequest();
        imageFileClipDropRequest.setImageFile(ImageUploadUtils.getImageFile(request.getImageUrl()));

        // 处理图片
        ClipDropImage clipDropImage = clipDropImageService.removeBackground(imageFileClipDropRequest);
        AppValidate.notNull(clipDropImage, ErrorCodeConstants.GENERATE_IMAGE_EMPTY);

        // 处理响应结果
        ImageDTO image = ImageConvert.INSTANCE.convert(clipDropImage);
        RemoveBackgroundResponse response = new RemoveBackgroundResponse();
        response.setOriginalUrl(request.getImageUrl());
        response.setImages(Collections.singletonList(image));
        log.info("去除图片背景【智能抠图】结束：响应结果: \n{}", JsonUtils.toJsonPrettyString(response));
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
    public Integer getCostPoints(RemoveBackgroundRequest request, RemoveBackgroundResponse response) {
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
    public void handleLogMessage(LogAppMessageCreateReqVO messageRequest, RemoveBackgroundRequest request, RemoveBackgroundResponse response) {
        messageRequest.setAnswerUnitPrice(ImageUtils.CD_PRICE);
        if (Objects.nonNull(response) && CollectionUtil.isNotEmpty(response.getImages())) {
            messageRequest.setTotalPrice(new BigDecimal("1").multiply(ImageUtils.CD_PRICE));
        }
    }
}
