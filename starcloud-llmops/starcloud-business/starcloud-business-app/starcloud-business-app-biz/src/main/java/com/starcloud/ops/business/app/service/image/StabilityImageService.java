package com.starcloud.ops.business.app.service.image;

import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.feign.request.StabilityImageRequest;
import com.starcloud.ops.business.app.feign.response.StabilityImage;

import java.util.List;

/**
 * 对 VSearch 的图片生成接口进行封装
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
public interface StabilityImageService {

    /**
     * 生成图片
     *
     * @param request 请求参数
     * @return 图片列表
     */
    List<StabilityImage> generate(StabilityImageRequest request);

    /**
     * 文字生成图片
     *
     * @param request 请求参数
     * @return 图片列表
     */
    List<ImageDTO> textToImage(ImageRequest request);
}
