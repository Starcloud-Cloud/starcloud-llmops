package com.starcloud.ops.business.app.service.image.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.RemoveTextRequest;
import com.starcloud.ops.business.app.api.image.vo.response.RemoveTextResponse;
import com.starcloud.ops.business.app.convert.image.ImageConvert;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.feign.request.clipdrop.ImageFileClipDropRequest;
import com.starcloud.ops.business.app.feign.dto.ClipDropImage;
import com.starcloud.ops.business.app.service.image.clipdrop.ClipDropImageService;
import com.starcloud.ops.business.app.service.image.strategy.ImageScene;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.app.api.AppValidate;
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
@ImageScene(AppSceneEnum.IMAGE_REMOVE_TEXT)
public class RemoveTextHandler extends BaseImageHandler<RemoveTextRequest, RemoveTextResponse> {

    /**
     * 图片处理引擎
     */
    private static final String ENGINE = "clip-drop-remove-text";

    @Resource
    private ClipDropImageService clipDropImageService;

    /**
     * 获取图片处理引擎
     *
     * @param request 请求
     */
    @Override
    public String obtainEngine(RemoveTextRequest request) {
        return ENGINE;
    }

    /**
     * 构建图片配置信息配置
     *
     * @param request 请求
     */
    @Override
    public void handleRequest(RemoveTextRequest request) {
        log.info("去除图片文字：请求参数：{}", JSONUtil.toJsonStr(request));
        String imageUrl = request.getImageUrl();
        String extension = ImageUploadUtils.getExtension(imageUrl);
        if (!"jpg".equals(extension) && !"jpeg".equals(extension) && !"png".equals(extension)) {
            throw ServiceExceptionUtil.exception(new ErrorCode(ErrorCodeConstants.EXECUTE_IMAGE_FEIGN_FAILURE.getCode(), "不支持的图片格式(仅支持jpg、jpeg、png)"));
        }
    }

    /**
     * 处理图片
     *
     * @param request 图片请求
     * @return 图片响应
     */
    @Override
    public RemoveTextResponse handleImage(RemoveTextRequest request) {
        log.info("去除图片文字开始：处理前数据：{}", JSONUtil.toJsonStr(request));
        // 处理请求参数
        ImageFileClipDropRequest imageFileClipDropRequest = new ImageFileClipDropRequest();
        imageFileClipDropRequest.setImageFile(ImageUploadUtils.getImageFile(request.getImageUrl()));

        // 处理图片
        ClipDropImage clipDropImage = clipDropImageService.removeText(imageFileClipDropRequest);
        AppValidate.notNull(clipDropImage, ErrorCodeConstants.GENERATE_IMAGE_EMPTY);

        // 处理响应参数
        ImageDTO image = ImageConvert.INSTANCE.convert(clipDropImage);
        RemoveTextResponse response = new RemoveTextResponse();
        response.setOriginalUrl(request.getImageUrl());
        response.setImages(Collections.singletonList(image));

        log.info("去除图片文字结束：响应结果：{}", JSONUtil.toJsonStr(response));
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
    public Integer getCostPoints(RemoveTextRequest request, RemoveTextResponse response) {
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
    public void handleLogMessage(LogAppMessageCreateReqVO messageRequest, RemoveTextRequest request, RemoveTextResponse response) {
        messageRequest.setAnswerUnitPrice(ImageUtils.CD_PRICE);
        if (Objects.nonNull(response) && CollectionUtil.isNotEmpty(response.getImages())) {
            messageRequest.setTotalPrice(new BigDecimal("1").multiply(ImageUtils.CD_PRICE));
        }
    }
}
