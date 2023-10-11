package com.starcloud.ops.business.app.service.image.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.image.vo.request.UpscaleImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.UpscaleImageResponse;
import com.starcloud.ops.business.app.convert.image.ImageConvert;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.vsearch.EngineEnum;
import com.starcloud.ops.business.app.feign.request.vsearch.VSearchUpscaleImageRequest;
import com.starcloud.ops.business.app.feign.response.VSearchImage;
import com.starcloud.ops.business.app.service.image.strategy.ImageScene;
import com.starcloud.ops.business.app.service.vsearch.VSearchService;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
     * 构建图片配置信息配置
     *
     * @param request 请求
     */
    @Override
    public void handleRequest(UpscaleImageRequest request) {
        log.info("放大/高清图片：请求参数处理开始：请求参数：{}", JSONUtil.toJsonStr(request));
        if (StringUtils.isBlank(request.getEngine())) {
            request.setEngine(EngineEnum.ESRGAN_V1_X2PLUS.getCode());
        }
        EngineEnum engineEnum = IEnumable.codeOf(request.getEngine(), EngineEnum.class);
        if (Objects.isNull(engineEnum)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_REQUEST_FAILURE, "不支持的放大/高清图片引擎");
        }
        switch (engineEnum) {
            case ESRGAN_V1_X2PLUS:
                request.setPrompt(null);
                request.setCfgScale(null);
                request.setSteps(null);
                request.setSeed(null);
                break;
            case STABLE_DIFFUSION_X4_LATENT_UPSCALER:
                if (Objects.isNull(request.getCfgScale())) {
                    request.setCfgScale(7.0);
                }
                if (Objects.isNull(request.getSteps())) {
                    request.setSteps(50);
                }
                if (Objects.isNull(request.getSeed())) {
                    request.setSeed(0L);
                }
                break;
            default:
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_REQUEST_FAILURE, "不支持的放大/高清图片引擎");
        }

        // 获取原始图像二进制数据
        byte[] imageBytes = ImageUploadUtils.getContent(request.getInitImage());

        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (Objects.isNull(image)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_REQUEST_FAILURE, "获取原始图像失败");
            }
            int width = image.getWidth();
            int height = image.getHeight();
            validateImageSize(width, height, engineEnum, Boolean.FALSE);
            request.setOriginalWidth(width);
            request.setOriginalHeight(height);
        } catch (IOException e) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_REQUEST_FAILURE, "获取原始图像失败");
        }

        // 高宽和放大倍数都为空，说明用户是使用默认放大倍数放大图片。2倍
        if (Objects.isNull(request.getWidth()) && Objects.isNull(request.getHeight()) && Objects.isNull(request.getMagnification())) {
            request.setMagnification(2);
        }
        // 放大倍数不为空，说明用户是使用放大倍数放大图片
        if (Objects.nonNull(request.getMagnification())) {
            // 校验图片大小
            BigDecimal magnification = new BigDecimal(String.valueOf(request.getMagnification()));
            int width = new BigDecimal(String.valueOf(request.getOriginalWidth())).multiply(magnification).intValue();
            int height = new BigDecimal(String.valueOf(request.getOriginalHeight())).multiply(magnification).intValue();
            validateImageSize(width, height, engineEnum, Boolean.TRUE);
            request.setWidth(width);
            request.setHeight(height);
        } else if (!Objects.isNull(request.getWidth()) && !Objects.isNull(request.getHeight())) {
            // 用户使用宽高放大图片
            validateImageSize(request.getWidth(), request.getHeight(), engineEnum, Boolean.TRUE);
            request.setWidth(request.getWidth());
            request.setHeight(request.getHeight());
        } else {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_REQUEST_FAILURE, "放大/高清图片失败, 请检查参数, 宽高和放大倍数不能同时为空");
        }

        log.info("放大/高清图片：请求参数处理结束：请求参数：{}", JSONUtil.toJsonStr(request));

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
        upscaleImageRequest.setEngine(request.getEngine());
        upscaleImageRequest.setPrompt(request.getPrompt());
        // 获取原始图像二进制数据
        byte[] imageBytes = ImageUploadUtils.getContent(request.getInitImage());
        String image = Base64.getEncoder().encodeToString(imageBytes);
        upscaleImageRequest.setInitImage(ImageUtils.handlerBase64Image(image));
        upscaleImageRequest.setHeight(request.getHeight());
        upscaleImageRequest.setCfgScale(request.getCfgScale());
        upscaleImageRequest.setSteps(request.getSteps());
        upscaleImageRequest.setSeed(request.getSeed());

        // 调用放大图片接口
        List<VSearchImage> vSearchImages = vSearchService.upscaleImage(upscaleImageRequest);
        AppValidate.notEmpty(vSearchImages, ErrorCodeConstants.GENERATE_IMAGE_EMPTY);

        // 处理响应结果
        UpscaleImageResponse response = new UpscaleImageResponse();
        response.setEngine(request.getEngine());
        response.setOriginalUrl(request.getInitImage());
        response.setMagnification(request.getMagnification());
        response.setOriginalHeight(request.getOriginalHeight());
        response.setOriginalWidth(request.getOriginalWidth());
        response.setWidth(request.getWidth());
        response.setHeight(request.getHeight());
        response.setImages(ImageConvert.INSTANCE.convert(vSearchImages));
        if (EngineEnum.STABLE_DIFFUSION_X4_LATENT_UPSCALER.getCode().equals(request.getEngine())) {
            response.setPrompt(request.getPrompt());
            response.setSteps(request.getSteps());
            response.setSeed(request.getSeed());
        }

        log.info("放大/高清图片结束：响应结果：{}", JSONUtil.toJsonStr(response));
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
    public void handleLogMessage(LogAppMessageCreateReqVO messageRequest, UpscaleImageRequest request, UpscaleImageResponse response) {
        messageRequest.setAnswerUnitPrice(ImageUtils.SD_PRICE);
        if (Objects.nonNull(response) && CollectionUtil.isNotEmpty(response.getImages())) {
            messageRequest.setTotalPrice(ImageUtils.countAnswerCredits(request).multiply(ImageUtils.CD_PRICE));
        }
        if (StringUtils.isNotBlank(request.getPrompt())) {
            messageRequest.setMessage(request.getPrompt());
        }
        messageRequest.setAiModel("stable-diffusion");
    }

    /**
     * 校验图片大小
     *
     * @param width  宽度
     * @param height 高度
     * @param engine 引擎
     */
    private void validateImageSize(int width, int height, EngineEnum engine, boolean isOut) {
        BigDecimal widthDecimal = new BigDecimal(String.valueOf(width));
        BigDecimal heightDecimal = new BigDecimal(String.valueOf(height));
        BigDecimal multiply = widthDecimal.multiply(heightDecimal);
        if (isOut) {
            if (EngineEnum.ESRGAN_V1_X2PLUS.equals(engine)) {
                if (multiply.compareTo(new BigDecimal("4194304")) > 0) {
                    throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_REQUEST_FAILURE, "图片大小不能超过 4194304(2048 x 2048)像素");
                }
            } else if (EngineEnum.STABLE_DIFFUSION_X4_LATENT_UPSCALER.equals(engine)) {
                if (multiply.compareTo(new BigDecimal("6291456")) > 0) {
                    throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_REQUEST_FAILURE, "图片大小不能超过 6291456(2048 x 3072)像素");
                }
            }
        } else {
            if (EngineEnum.ESRGAN_V1_X2PLUS.equals(engine)) {
                if (multiply.compareTo(new BigDecimal("1048576")) > 0) {
                    throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_REQUEST_FAILURE, "图片大小不能超过 1048576(1024 x 1024)像素");
                }
            } else if (EngineEnum.STABLE_DIFFUSION_X4_LATENT_UPSCALER.equals(engine)) {
                if (multiply.compareTo(new BigDecimal("393216")) > 0) {
                    throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_REQUEST_FAILURE, "图片大小不能超过 393216(512 x 768)像素");
                }
            }
        }
    }
}
