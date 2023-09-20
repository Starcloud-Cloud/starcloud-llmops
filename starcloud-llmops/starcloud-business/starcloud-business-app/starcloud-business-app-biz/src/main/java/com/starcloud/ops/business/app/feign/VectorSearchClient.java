package com.starcloud.ops.business.app.feign;

import com.starcloud.ops.business.app.feign.request.VectorSearchImageRequest;
import com.starcloud.ops.business.app.feign.response.VectorSearchImage;
import com.starcloud.ops.business.app.feign.response.VectorSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Vector Search Open Feign Client <br>
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-14
 */
@FeignClient(name = "${feign.remote.vsearch.name}", url = "${feign.remote.vsearch.url}")
public interface VectorSearchClient {

    /**
     * 生成图片通用接口
     *
     * @return 生成图片结果
     */
    @PostMapping(value = "/generateImage")
    VectorSearchResponse<List<VectorSearchImage>> generateImage(@Validated @RequestBody VectorSearchImageRequest request);

}
