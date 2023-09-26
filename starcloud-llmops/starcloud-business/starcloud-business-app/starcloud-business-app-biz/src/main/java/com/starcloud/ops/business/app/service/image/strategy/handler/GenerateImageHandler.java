package com.starcloud.ops.business.app.service.image.strategy.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.image.vo.request.GenerateImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.GenerateImageResponse;
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
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * 生成图片处理器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-22
 */
@Slf4j
@Component
@ImageScene(AppSceneEnum.WEB_IMAGE)
public class GenerateImageHandler extends BaseImageHandler<GenerateImageRequest, GenerateImageResponse> {

    @Resource
    private VSearchService vSearchService;

    /**
     * 构建图片请求信息
     *
     * @param request 请求
     */
    @Override
    public void handleRequest(GenerateImageRequest request) {
        log.info("GenerateImageHandler handleRequest: 处理生成图片请求开始：处理前数据：{}", JSONUtil.toJsonStr(request));
        // 生成图片的引擎
        if (StringUtils.isBlank(request.getEngine())) {
            request.setEngine(EngineEnum.STABLE_DIFFUSION_XL_BETA_V2_2_2.getCode());
        }
        // 初始化图片
        if (StringUtils.isNotBlank(request.getInitImage())) {
            request.setInitImage(ImageUtils.handlerBase64Image(request.getInitImage()));
            if (Objects.isNull(request.getImageStrength())) {
                request.setImageStrength(0.35);
            }
        }
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
            request.setCfgScale(7.0);
        }
        // 图片的 sampler
        if (Objects.isNull(request.getSampler())) {
            request.setSampler(SamplerEnum.K_DPMPP_2M.getCode());
        }
        // 图片的 steps
        if (Objects.isNull(request.getSteps())) {
            request.setSteps(50);
        }
        // 图片的 samples
        if (Objects.isNull(request.getSamples())) {
            request.setSamples(1);
        }
        log.info("GenerateImageHandler handleRequest: 处理生成图片请求结束：处理后数据：{}", JSONUtil.toJsonStr(request));
    }

    /**
     * 处理图片
     *
     * @param request 图片请求
     * @return 图片响应
     */
    @Override
    public GenerateImageResponse handle(GenerateImageRequest request) {
        log.info("GenerateImageHandler handle: 处理生成图片请求开始：处理前数据：{}", JSONUtil.toJsonStr(request));
        VSearchImageRequest vSearchImageRequest = VSearchConvert.INSTANCE.convert(request);
        List<VSearchImage> imageList = vSearchService.generateImage(vSearchImageRequest);
        if (CollectionUtil.isEmpty(imageList)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_IMAGE_FAIL, "生成图片失败");
        }
        GenerateImageResponse response = new GenerateImageResponse();
        response.setPrompt(request.getPrompt());
        response.setNegativePrompt(ImageUtils.handleNegativePrompt(request.getNegativePrompt(), Boolean.FALSE));
        response.setEngine(request.getEngine());
        response.setWidth(request.getWidth());
        response.setHeight(request.getHeight());
        response.setSteps(request.getSteps());
        response.setStylePreset(request.getStylePreset());
        response.setImages(ImageConvert.INSTANCE.convert(imageList));
        log.info("GenerateImageHandler handle: 处理生成图片请求结束：处理后结果：{}", JSONUtil.toJsonStr(response));
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
    public void handleLogMessage(LogAppMessageCreateReqVO messageRequest, GenerateImageRequest request, GenerateImageResponse response) {
        messageRequest.setMessage(request.getPrompt());
        messageRequest.setAiModel("stable-diffusion");
    }


}
