package com.starcloud.ops.business.app.feign;

import com.starcloud.ops.business.app.feign.request.vsearch.VSearchImageRequest;
import com.starcloud.ops.business.app.feign.request.vsearch.VSearchUpscaleImageRequest;
import com.starcloud.ops.business.app.feign.response.VSearchImage;
import com.starcloud.ops.business.app.feign.response.VSearchResponse;
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
public interface VSearchClient {

    /**
     * 生成图片通用接口
     *
     * @param request 生成图片请求
     * @return 生成图片结果
     */
    @PostMapping(value = "/generateImage")
    VSearchResponse<List<VSearchImage>> generateImage(@Validated @RequestBody VSearchImageRequest request);

    /**
     * 图片放大接口
     *
     * @param request 图片放大请求
     * @return 图片放大结果
     */
    @PostMapping(value = "/upscaleImage")
    VSearchResponse<List<VSearchImage>> upscaleImage(@Validated @RequestBody VSearchUpscaleImageRequest request);
}
