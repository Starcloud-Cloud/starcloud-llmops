package com.starcloud.ops.business.app.service.image.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.starcloud.ops.business.app.api.image.vo.request.VariantsImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.VariantsImageResponse;
import com.starcloud.ops.business.app.convert.image.ImageConvert;
import com.starcloud.ops.business.app.convert.vsearch.VSearchConvert;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.vsearch.EngineEnum;
import com.starcloud.ops.business.app.enums.vsearch.SamplerEnum;
import com.starcloud.ops.business.app.feign.request.vsearch.VSearchImageRequest;
import com.starcloud.ops.business.app.feign.response.VSearchImage;
import com.starcloud.ops.business.app.service.image.strategy.ImageScene;
import com.starcloud.ops.business.app.service.vsearch.VSearchService;
import com.starcloud.ops.business.app.util.ImageUploadUtils;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

/**
 * 图片变体处理器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-11
 */
@Slf4j
@Component
@ImageScene(AppSceneEnum.IMAGE_VARIANTS)
public class VariantsImageHandler extends BaseImageHandler<VariantsImageRequest, VariantsImageResponse> {

    @Resource
    private VSearchService vSearchService;

    /**
     * 获取图片处理引擎
     *
     * @param request 请求
     */
    @Override
    public String obtainEngine(VariantsImageRequest request) {
        // 生成图片的引擎
        if (StringUtils.isBlank(request.getEngine())) {
            request.setEngine(EngineEnum.STABLE_DIFFUSION_XL_1024_V1_0.getCode());
        }
        return request.getEngine();
    }

    /**
     * 构建图片配置信息配置
     *
     * @param request 请求
     */
    @Override
    public void handleRequest(VariantsImageRequest request) {
        log.info("VariantsImageHandler handleRequest: 处理裂变图片请求开始：处理前数据：{}", JSONUtil.toJsonStr(request));

        // 初始化图片
        if (Objects.isNull(request.getImageStrength())) {
            request.setImageStrength(0.6);
        }
        request.setPrompt(ImageUtils.handlePrompt(request.getPrompt(), Boolean.TRUE));
        // 反义词
        request.setNegativePrompt(ImageUtils.handleNegativePrompt(request.getNegativePrompt(), Boolean.TRUE));
        // 图片的宽度
        if (Objects.isNull(request.getWidth())) {
            request.setWidth(512);
        }
        // 图片的高度
        if (Objects.isNull(request.getHeight())) {
            request.setHeight(512);
        }
        // 图片的 cfgScale
        if (Objects.isNull(request.getCfgScale())) {
            request.setCfgScale(8.0);
        }
        // 图片的 sampler
        if (Objects.isNull(request.getSampler())) {
            request.setSampler(SamplerEnum.K_DPMPP_2M.getCode());
        }
        // 图片的 steps
        if (Objects.isNull(request.getSteps())) {
            request.setSteps(30);
        }

        log.info("VariantsImageHandler handleRequest: 处理裂变图片请求结束：处理后数据：{}", JSONUtil.toJsonStr(request));
    }

    /**
     * 处理图片
     *
     * @param request 图片请求
     * @return 图片响应
     */
    @Override
    public VariantsImageResponse handleImage(VariantsImageRequest request) {
        log.info("VariantsImageHandler handle: 裂变图片请求开始...");
        // 处理初始化图片
        String initImage = request.getInitImage();
        request.setInitImage(ImageUploadUtils.handleImageToBase64(initImage));

        // 调用 VSearch 接口
        VSearchImageRequest vSearchImageRequest = VSearchConvert.INSTANCE.convert(request);
        List<VSearchImage> imageList = vSearchService.generateImage(vSearchImageRequest);
        AppValidate.notEmpty(imageList, ErrorCodeConstants.GENERATE_IMAGE_EMPTY);

        // 处理响应
        VariantsImageResponse response = new VariantsImageResponse();
        response.setOriginalUrl(initImage);
        response.setPrompt(ImageUtils.handlePrompt(request.getPrompt(), Boolean.FALSE));
        response.setNegativePrompt(ImageUtils.handleNegativePrompt(request.getNegativePrompt(), Boolean.FALSE));
        response.setEngine(request.getEngine());
        response.setWidth(request.getWidth());
        response.setHeight(request.getHeight());
        response.setSteps(request.getSteps());
        response.setStylePreset(request.getStylePreset());
        response.setImages(ImageConvert.INSTANCE.convert(imageList));

        log.info("VariantsImageHandler handle: 裂变图片请求结束... 响应结果：{}", JSONUtil.toJsonStr(response));
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
    public Integer getCostPoints(VariantsImageRequest request, VariantsImageResponse response) {
        return 4;
    }

    /**
     * 处理日志消息
     *
     * @param messageRequest 日志信息
     * @param request        请求
     * @param response       响应
     */
    @Override
    public void handleLogMessage(LogAppMessageCreateReqVO messageRequest, VariantsImageRequest request, VariantsImageResponse response) {
        messageRequest.setAnswerUnitPrice(ImageUtils.SD_PRICE);
        if (Objects.nonNull(response) && CollectionUtil.isNotEmpty(response.getImages())) {
            messageRequest.setTotalPrice(ImageUtils.countAnswerCredits(request).multiply(ImageUtils.SD_PRICE));
        }
        messageRequest.setMessage(request.getPrompt());
        messageRequest.setAiModel("stable-diffusion");
    }

}
