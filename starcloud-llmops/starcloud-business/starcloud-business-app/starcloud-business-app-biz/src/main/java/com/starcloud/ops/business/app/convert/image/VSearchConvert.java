package com.starcloud.ops.business.app.convert.image;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.dto.TextPrompt;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.feign.request.VSearchImageRequest;
import com.starcloud.ops.business.app.feign.response.VSearchImage;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
     * VSearchImage 转 ImageDTO
     *
     * @param vSearchImage vSearchImage
     * @return ImageDTO
     */
    ImageDTO convert(VSearchImage vSearchImage);

    /**
     * List<VSearchImage> 转 List<ImageDTO>
     *
     * @param vSearchImageList vSearchImageList
     * @return List<ImageDTO>
     */
    default List<ImageDTO> convert(List<VSearchImage> vSearchImageList) {
        return CollectionUtil.emptyIfNull(vSearchImageList).stream().filter(Objects::nonNull).map(this::convert).collect(Collectors.toList());
    }

    /**
     * TextToImageRequest 转 VSearchImageRequest
     *
     * @param request 请求参数
     * @return VSearchImageRequest
     */
    default VSearchImageRequest convert(ImageRequest request) {
        VSearchImageRequest vSearchImageRequest = new VSearchImageRequest();
        vSearchImageRequest.setEngine(request.getEngine());
        vSearchImageRequest.setPrompts(Collections.singletonList(TextPrompt.ofDefault(request.getPrompt())));
        if (StringUtils.isNotBlank(request.getInitImage())) {

            String initImage = request.getInitImage();
            if (initImage.contains("data:image/png;base64,")) {
                initImage = initImage.replace("data:image/png;base64,", "");
            }
            initImage = initImage.replaceAll("\r|\n", "").trim();
            byte[] decode = Base64.getDecoder().decode(initImage);
            String binary = new String(decode, StandardCharsets.UTF_8);
            vSearchImageRequest.setInitImage(binary);
            if (request.getImageStrength() == null) {
                vSearchImageRequest.setStartSchedule(0.65);
            } else {
                vSearchImageRequest.setStartSchedule(1 - request.getImageStrength());
            }
        }
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
