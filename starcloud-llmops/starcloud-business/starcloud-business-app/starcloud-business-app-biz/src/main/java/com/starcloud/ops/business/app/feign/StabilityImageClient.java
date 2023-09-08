package com.starcloud.ops.business.app.feign;

import com.starcloud.ops.business.app.feign.config.StabilityFeignConfiguration;
import com.starcloud.ops.business.app.feign.request.StabilityImageRequest;
import com.starcloud.ops.business.app.feign.response.StabilityImage;
import com.starcloud.ops.business.app.feign.response.ImageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Stability AI 生成图片 <br>
 * <a href="https://platform.stability.ai/">https://platform.stability.ai/</a>
 *
 * @author nacoyer
 */
@FeignClient(name = "${feign.remote.stability.name}", url = "${feign.remote.stability.url}", path = "/stability", configuration = StabilityFeignConfiguration.class)
public interface StabilityImageClient {

    /**
     * 生成图片
     *
     * @return 生成图片结果
     */
    @PostMapping(value = "/generateImage")
    ImageResponse<List<StabilityImage>> generateImage(@Validated @RequestBody StabilityImageRequest request);

}
