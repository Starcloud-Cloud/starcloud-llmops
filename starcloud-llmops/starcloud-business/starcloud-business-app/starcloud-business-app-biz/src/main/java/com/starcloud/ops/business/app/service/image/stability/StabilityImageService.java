package com.starcloud.ops.business.app.service.image.stability;

import com.starcloud.ops.business.app.feign.request.stability.MaskingStabilityImageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 对 Stability 的图片生成接口进行封装
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
public interface StabilityImageService {


    ResponseEntity<String> masking(String engineId, MaskingStabilityImageRequest request);
}
