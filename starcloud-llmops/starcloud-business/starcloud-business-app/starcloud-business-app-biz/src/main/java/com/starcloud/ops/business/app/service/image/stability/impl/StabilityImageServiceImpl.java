package com.starcloud.ops.business.app.service.image.stability.impl;

import com.starcloud.ops.business.app.feign.StabilityImageClient;
import com.starcloud.ops.business.app.feign.request.stability.MaskingStabilityImageRequest;
import com.starcloud.ops.business.app.service.image.stability.StabilityImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 对接 VSearch 的图片服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Slf4j
@Service
public class StabilityImageServiceImpl implements StabilityImageService {

    @Resource
    private StabilityImageClient stabilityImageClient;

    @Override
    public ResponseEntity<String> masking(String engineId, MaskingStabilityImageRequest request) {
        ResponseEntity<String> masking = stabilityImageClient.masking(engineId, request);
        String body = masking.getBody();
        log.info("成功：body: {}", body);
        return null;
    }
}
