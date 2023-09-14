package com.starcloud.ops.business.app.feign;

import com.starcloud.ops.business.app.feign.config.StabilityFeignConfiguration;
import com.starcloud.ops.business.app.feign.request.stability.MaskingStabilityImageRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Stability AI 生成图片 <br>
 * <a href="https://platform.stability.ai/">https://platform.stability.ai/</a>
 *
 * @author nacoyer
 */
@FeignClient(name = "${feign.remote.stability.name}", url = "${feign.remote.stability.url}", path = "/v1", configuration = StabilityFeignConfiguration.class)
public interface StabilityImageClient {


    @PostMapping("/generation/{engine_id}/image-to-image/masking")
    ResponseEntity<String> masking(@PathVariable("engine_id") String engineId, @RequestBody MaskingStabilityImageRequest request);
}
