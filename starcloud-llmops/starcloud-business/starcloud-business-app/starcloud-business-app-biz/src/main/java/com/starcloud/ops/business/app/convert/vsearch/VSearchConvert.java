package com.starcloud.ops.business.app.convert.vsearch;

import com.starcloud.ops.business.app.api.image.dto.TextPrompt;
import com.starcloud.ops.business.app.api.image.vo.request.GenerateImageRequest;
import com.starcloud.ops.business.app.api.image.vo.request.VariantsImageRequest;
import com.starcloud.ops.business.app.feign.request.vsearch.VSearchImageRequest;
import com.starcloud.ops.business.app.util.ImageUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * 对 VSearch 接口实体转换的封装
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Mapper
public interface VSearchConvert {

    /**
     * VsearchConvert
     */
    VSearchConvert INSTANCE = Mappers.getMapper(VSearchConvert.class);

    /**
     * TextToImageRequest 转 VSearchImageRequest
     *
     * @param request 请求参数
     * @return VSearchImageRequest
     */
    default VSearchImageRequest convert(GenerateImageRequest request) {
        VSearchImageRequest vSearchImageRequest = new VSearchImageRequest();
        vSearchImageRequest.setEngine(request.getEngine());

        // prompts
        List<TextPrompt> prompts = new ArrayList<>();
        prompts.add(TextPrompt.ofDefault(request.getPrompt()));

        // 反义词
        String negativePrompt = request.getNegativePrompt();
        if (StringUtils.isNotBlank(negativePrompt)) {
            prompts.add(TextPrompt.ofNegative(negativePrompt));
        }

        // 初始化图片
        if (StringUtils.isNotBlank(request.getInitImage())) {
            vSearchImageRequest.setInitImage(request.getInitImage());
            if (request.getImageStrength() != null) {
                vSearchImageRequest.setStartSchedule(request.getImageStrength());
                if (request.getImageStrength() <= 0.1) {
                    vSearchImageRequest.setStartSchedule(0.1);
                }
            } else {
                vSearchImageRequest.setStartSchedule(0.6);
            }
        }

        vSearchImageRequest.setPrompts(prompts);
        vSearchImageRequest.setHeight(request.getHeight());
        vSearchImageRequest.setWidth(request.getWidth());
        vSearchImageRequest.setCfgScale(request.getCfgScale());
        vSearchImageRequest.setSampler(request.getSampler());
        vSearchImageRequest.setSteps(request.getSteps());
        vSearchImageRequest.setSeed(request.getSeed());
        vSearchImageRequest.setSamples(request.getSamples());
        vSearchImageRequest.setGuidancePreset(request.getGuidancePreset());
        vSearchImageRequest.setGuidanceStrength(request.getGuidanceStrength());
        vSearchImageRequest.setStylePreset(request.getStylePreset());
        return vSearchImageRequest;
    }

    /**
     * VariantsImageRequest 转 VSearchImageRequest
     *
     * @param request 请求参数
     * @return VSearchImageRequest
     */
    default VSearchImageRequest convert(VariantsImageRequest request) {
        VSearchImageRequest vSearchImageRequest = new VSearchImageRequest();
        vSearchImageRequest.setEngine(request.getEngine());

        // prompts
        List<TextPrompt> prompts = new ArrayList<>();
        prompts.add(TextPrompt.ofDefault(request.getPrompt()));

        // 反义词
        String negativePrompt = request.getNegativePrompt();
        if (StringUtils.isNotBlank(negativePrompt)) {
            prompts.add(TextPrompt.ofNegative(negativePrompt));
        }

        vSearchImageRequest.setInitImage(ImageUtils.handlerBase64Image(request.getInitImage()));

        if (request.getImageStrength() != null) {
            vSearchImageRequest.setStartSchedule(request.getImageStrength());
            if (request.getImageStrength() <= 0.1) {
                vSearchImageRequest.setStartSchedule(0.1);
            }
        } else {
            vSearchImageRequest.setStartSchedule(0.6);
        }

        vSearchImageRequest.setPrompts(prompts);
        vSearchImageRequest.setHeight(request.getHeight());
        vSearchImageRequest.setWidth(request.getWidth());
        vSearchImageRequest.setCfgScale(request.getCfgScale());
        vSearchImageRequest.setSampler(request.getSampler());
        vSearchImageRequest.setSteps(request.getSteps());
        vSearchImageRequest.setSeed(request.getSeed());
        vSearchImageRequest.setSamples(4);
        vSearchImageRequest.setStylePreset(request.getStylePreset());
        return vSearchImageRequest;
    }


}
