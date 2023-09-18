package com.starcloud.ops.business.app.feign;

import com.starcloud.ops.business.app.feign.request.VSearchImageRequest;
import com.starcloud.ops.business.app.feign.response.VSearchImage;
import com.starcloud.ops.business.app.feign.response.VSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * name  没有注册中心 可以随意配置
 * url   调用的服务器域名
 *
 * @author nacoyer
 */
@FeignClient(name = "${feign.remote.vector-search.name}", url = "${feign.remote.vector-search.url}")
public interface VSearchClient {

    @GetMapping(value = "/")
    String hello();

    /**
     * 生成图片
     *
     * @return 生成图片结果
     */
    @PostMapping(value = "/generateImage")
    VSearchResponse<List<VSearchImage>> generateImage(@Validated @RequestBody VSearchImageRequest request);

}
