package com.starcloud.ops.business.app.convert.image;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.image.dto.TextPrompt;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.vsearch.EngineEnum;
import com.starcloud.ops.business.app.enums.vsearch.SamplerEnum;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Mapper
public interface ImageConvert {

    ImageConvert INSTANCE = Mappers.getMapper(ImageConvert.class);

    /**
     * 将 ImageRequest 转换成 ImageConfigEntity
     *
     * @param request request
     * @return ImageConfigEntity
     */
    default ImageConfigEntity convert(ImageRequest request) {
        ImageConfigEntity entity = new ImageConfigEntity();

        // prompts
        if (StringUtils.isBlank(request.getPrompt())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IMAGE_PROMPT_REQUIRED);
        }
        List<TextPrompt> prompts = new ArrayList<>();
        prompts.add(TextPrompt.ofDefault(request.getPrompt()));
        // 反义词
        if (StringUtils.isNotBlank(request.getNegativePrompt())) {
            prompts.add(TextPrompt.ofNegative(request.getNegativePrompt()));
        }
        entity.setPrompts(prompts);

        // 生成图片的引擎
        if (StringUtils.isBlank(request.getEngine())) {
            entity.setEngine(EngineEnum.STABLE_DIFFUSION_XL_BETA_V2_2_2.getCode());
            request.setEngine(EngineEnum.STABLE_DIFFUSION_XL_BETA_V2_2_2.getCode());
        } else {
            entity.setEngine(request.getEngine());
        }

        // 初始化图片
        if (StringUtils.isNotBlank(request.getInitImage())) {
            entity.setInitImage(request.getInitImage());
            if (request.getImageStrength() == null) {
                entity.setStartSchedule(0.65);
                request.setImageStrength(0.35);
            } else {
                entity.setStartSchedule(1 - request.getImageStrength());
            }
        }

        // 图片的宽度
        if (request.getWidth() == null) {
            entity.setWidth(512);
            request.setWidth(512);
        } else {
            entity.setWidth(request.getWidth());
        }

        // 图片的高度
        if (request.getHeight() == null) {
            entity.setHeight(512);
            request.setHeight(512);
        } else {
            entity.setHeight(request.getHeight());
        }

        // 图片的 cfgScale
        if (request.getCfgScale() == null) {
            entity.setCfgScale(7.0);
            request.setCfgScale(7.0);
        } else {
            entity.setCfgScale(request.getCfgScale());
        }

        // 图片的 sampler
        if (request.getSampler() == null) {
            entity.setSampler(SamplerEnum.K_DPMPP_2M.getCode());
            request.setSampler(SamplerEnum.K_DPMPP_2M.getCode());
        } else {
            entity.setSampler(request.getSampler());
        }

        // 图片的 steps
        if (request.getSteps() == null) {
            entity.setSteps(50);
            request.setSteps(50);
        } else {
            entity.setSteps(request.getSteps());
        }

        // 图片的 samples
        if (request.getSamples() == null) {
            entity.setSamples(1);
            request.setSamples(1);
        } else {
            entity.setSamples(request.getSamples());
        }

        if (request.getSeed() != null) {
            entity.setSeed(request.getSeed());
        }

        if (request.getGuidancePreset() != null) {
            entity.setGuidancePreset(request.getGuidancePreset());
        }

        if (request.getGuidanceStrength() != null) {
            entity.setGuidanceStrength(request.getGuidanceStrength());
        }

        if (StringUtils.isNotBlank(request.getStylePreset())) {
            entity.setStylePreset(request.getStylePreset());
        }

        return entity;
    }

}
