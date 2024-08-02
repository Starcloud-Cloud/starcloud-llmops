package com.starcloud.ops.business.app.service.image.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.image.vo.request.UpscaleImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.UpscaleImageResponse;
import com.starcloud.ops.business.app.convert.image.ImageConvert;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.vsearch.EngineEnum;
import com.starcloud.ops.business.app.feign.dto.VSearchImage;
import com.starcloud.ops.business.app.feign.request.vsearch.VSearchUpscaleImageRequest;
import com.starcloud.ops.business.app.service.image.strategy.ImageScene;
import com.starcloud.ops.business.app.service.vsearch.VSearchService;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-25
 */
@Slf4j
@Component
@ImageScene(AppSceneEnum.IMAGE_UPSCALING)
public class UpscaleImageHandler extends BaseImageHandler<UpscaleImageRequest, UpscaleImageResponse> {

    @Resource
    private VSearchService vSearchService;

    /**
     * 获取图片处理引擎
     *
     * @param request 请求
     */
    @Override
    public String obtainEngine(UpscaleImageRequest request) {
        return EngineEnum.ESRGAN_V1_X2PLUS.getCode();
    }

    /**
     * 构建图片配置信息配置
     *
     * @param request 请求
     */
    @Override
    public void handleRequest(UpscaleImageRequest request) {
        // 获取原始图像的BufferedImage
        BufferedImage originalImage = ImageUploadUtils.getBufferedImage(request.getInitImage());
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        validateImageSize(originalWidth, originalHeight, Boolean.FALSE);

        // 高宽和放大倍数都为空，说明用户是使用默认放大倍数放大图片。2倍
        if (Objects.isNull(request.getWidth()) && Objects.isNull(request.getHeight()) && Objects.isNull(request.getMagnification())) {
            request.setMagnification(2);
        }
        // 放大倍数不为空，说明用户是使用放大倍数放大图片
        if (Objects.nonNull(request.getMagnification())) {
            BigDecimal magnification = new BigDecimal(String.valueOf(request.getMagnification()));
            int newWidth = new BigDecimal(String.valueOf(originalWidth)).multiply(magnification).intValue();
            int newHeight = new BigDecimal(String.valueOf(originalHeight)).multiply(magnification).intValue();
            validateImageSize(newWidth, newHeight, Boolean.TRUE);
            request.setWidth(newWidth);
            request.setHeight(newHeight);
        } else if (!Objects.isNull(request.getWidth()) && !Objects.isNull(request.getHeight())) {
            // 用户使用宽高放大图片
            validateImageSize(request.getWidth(), request.getHeight(), Boolean.TRUE);
            request.setWidth(request.getWidth());
            request.setHeight(request.getHeight());
        } else {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_REQUEST_FAILURE, "放大/高清图片失败, 请检查参数, 宽高和放大倍数不能同时为空");
        }

        request.setOriginalWidth(originalWidth);
        request.setOriginalHeight(originalHeight);

    }

    /**
     * 处理图片
     *
     * @param request 图片请求
     * @return 图片响应
     */
    @Override
    public UpscaleImageResponse handleImage(UpscaleImageRequest request) {
        log.info("放大/高清图片开始...");
        // 处理请求参数
        VSearchUpscaleImageRequest upscaleImageRequest = new VSearchUpscaleImageRequest();
        upscaleImageRequest.setEngine(this.obtainEngine(request));
        // 获取原始图像二进制数据
        byte[] imageBytes = ImageUploadUtils.graphicsImage64xToByteArray(request.getInitImage());
        String image = Base64.getEncoder().encodeToString(imageBytes);

        upscaleImageRequest.setInitImage(ImageUtils.handlerBase64Image(image));
        upscaleImageRequest.setWidth(request.getWidth());

        // 调用放大图片接口
        List<VSearchImage> vSearchImages = vSearchService.upscaleImage(upscaleImageRequest);
        AppValidate.notEmpty(vSearchImages, ErrorCodeConstants.GENERATE_IMAGE_EMPTY);

        // 处理响应结果
        UpscaleImageResponse response = new UpscaleImageResponse();
        response.setOriginalUrl(request.getInitImage());
        response.setMagnification(request.getMagnification());
        response.setOriginalHeight(request.getOriginalHeight());
        response.setOriginalWidth(request.getOriginalWidth());
        response.setWidth(request.getWidth());
        response.setHeight(request.getHeight());
        response.setImages(ImageConvert.INSTANCE.convert(vSearchImages));

        log.info("放大/高清图片结束：响应结果: \n{}", JsonUtils.toJsonPrettyString(response));
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
    public Integer getCostPoints(UpscaleImageRequest request, UpscaleImageResponse response) {
        return 2;
    }

    /**
     * 处理日志消息
     *
     * @param messageRequest 日志信息
     * @param request        请求
     * @param response       响应
     */
    @Override
    public void handleLogMessage(LogAppMessageCreateReqVO messageRequest, UpscaleImageRequest request, UpscaleImageResponse response) {
        messageRequest.setAnswerUnitPrice(ImageUtils.SD_PRICE);
        if (Objects.nonNull(response) && CollectionUtil.isNotEmpty(response.getImages())) {
            messageRequest.setTotalPrice(ImageUtils.countAnswerCredits(request).multiply(ImageUtils.CD_PRICE));
        }
    }

    /**
     * 校验图片大小
     *
     * @param width  宽度
     * @param height 高度
     */
    private void validateImageSize(int width, int height, boolean isOut) {
        BigDecimal widthDecimal = new BigDecimal(String.valueOf(width));
        BigDecimal heightDecimal = new BigDecimal(String.valueOf(height));
        BigDecimal multiply = widthDecimal.multiply(heightDecimal);
        if (isOut) {
            if (multiply.compareTo(new BigDecimal("4194304")) > 0) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_REQUEST_FAILURE, "图片大小不能超过 4194304(2048 x 2048)像素");
            }
        } else {
            if (multiply.compareTo(new BigDecimal("1048576")) > 0) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_REQUEST_FAILURE, "图片大小不能超过 1048576(1024 x 1024)像素");
            }
        }
    }
}
