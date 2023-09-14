package com.starcloud.ops.business.app.service.vsearch;

import com.starcloud.ops.business.app.feign.request.VectorSearchImageRequest;
import com.starcloud.ops.business.app.feign.response.VectorSearchImage;

import java.util.List;

/**
 * Vector Search Service <br>
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-14
 */
public interface VectorSearchService {

    /**
     * 生成图片
     *
     * @param request 请求参数
     * @return 图片列表
     */
    List<VectorSearchImage> generateImage(VectorSearchImageRequest request);

}
