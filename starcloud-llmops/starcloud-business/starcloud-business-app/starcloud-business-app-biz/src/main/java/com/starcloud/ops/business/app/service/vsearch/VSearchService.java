package com.starcloud.ops.business.app.service.vsearch;

import com.starcloud.ops.business.app.feign.request.vsearch.VSearchImageRequest;
import com.starcloud.ops.business.app.feign.request.vsearch.VSearchUpscaleImageRequest;
import com.starcloud.ops.business.app.feign.dto.VSearchImage;

import java.util.List;

/**
 * Vector Search Service <br>
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-14
 */
public interface VSearchService {

    /**
     * 生成图片
     *
     * @param request 请求参数
     * @return 图片列表
     */
    List<VSearchImage> generateImage(VSearchImageRequest request);

    /**
     * 图片放大
     *
     * @param request 请求参数
     * @return 图片列表
     */
    List<VSearchImage> upscaleImage(VSearchUpscaleImageRequest request);

}
