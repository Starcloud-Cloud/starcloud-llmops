package com.starcloud.ops.business.app.convert.vsearch;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.dto.TextPrompt;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.feign.request.VectorSearchImageRequest;
import com.starcloud.ops.business.app.feign.response.VectorSearchImage;
import com.starcloud.ops.business.app.util.ImageUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 对 VectorSearch 接口实体转换的封装
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Mapper
public interface VectorSearchConvert {

    /**
     * VsearchConvert
     */
    VectorSearchConvert INSTANCE = Mappers.getMapper(VectorSearchConvert.class);

    /**
     * VSearchImage 转 ImageDTO
     *
     * @param vectorSearchImage vSearchImage
     * @return ImageDTO
     */
    ImageDTO convert(VectorSearchImage vectorSearchImage);

    /**
     * List<VSearchImage> 转 List<ImageDTO>
     *
     * @param vectorSearchImageList vSearchImageList
     * @return List<ImageDTO>
     */
    default List<ImageDTO> convert(List<VectorSearchImage> vectorSearchImageList) {
        return CollectionUtil.emptyIfNull(vectorSearchImageList).stream().filter(Objects::nonNull).map(this::convert).collect(Collectors.toList());
    }

    /**
     * TextToImageRequest 转 VSearchImageRequest
     *
     * @param request 请求参数
     * @return VSearchImageRequest
     */
    default VectorSearchImageRequest convert(ImageRequest request) {
        VectorSearchImageRequest vSearchImageRequest = new VectorSearchImageRequest();
        vSearchImageRequest.setEngine(request.getEngine());

        // prompts
        String prompt = request.getPrompt();
        if (StringUtils.isBlank(prompt)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.IMAGE_PROMPT_REQUIRED);
        }
        List<TextPrompt> prompts = new ArrayList<>();
        prompts.add(TextPrompt.ofDefault(prompt));

        // 反义词
        String negativePrompt = request.getNegativePrompt();
        if (StringUtils.isNotBlank(negativePrompt)) {
            prompts.add(TextPrompt.ofNegative(negativePrompt));
        }

        // 初始化图片
        if (StringUtils.isNotBlank(request.getInitImage())) {
            vSearchImageRequest.setInitImage(ImageUtils.handlerBase64Image(request.getInitImage()));
            if (request.getImageStrength() != null) {
                vSearchImageRequest.setStartSchedule(1 - request.getImageStrength());
            } else {
                vSearchImageRequest.setStartSchedule(0.65);
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


}
