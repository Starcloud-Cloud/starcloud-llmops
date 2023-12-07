package com.starcloud.ops.business.core.config.oss;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Slf4j
@Configuration
public class OssConfiguration {

    @Bean
    public OSS ossClient(OssProperties properties) {
        String endpoint = properties.getEndpoint();
        if (StringUtils.isBlank(endpoint)) {
            throw new IllegalArgumentException("初始化阿里云OSS失败：endpoint 为必填！");
        }
        String accessKey = properties.getAccessKey();
        if (StringUtils.isBlank(accessKey)) {
            throw new IllegalArgumentException("初始化阿里云OSS失败：accessKey 为必填！");
        }
        String secretKey = properties.getSecretKey();
        if (StringUtils.isBlank(secretKey)) {
            throw new IllegalArgumentException("初始化阿里云OSS失败：secretKey 为必填！");
        }
        if (StringUtils.isBlank(properties.getBucket())) {
            throw new IllegalArgumentException("初始化阿里云OSS失败：bucket 为必填！");
        }

        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSupportCname(properties.getSupportCname());
        log.info("初始化阿里云OSS：是否支持CNAME: {}, Endpoint: {}, Bucket: {}", properties.getSupportCname(), endpoint, properties.getBucket());
        return new OSSClientBuilder().build(endpoint, accessKey, secretKey, clientBuilderConfiguration);
    }
}
