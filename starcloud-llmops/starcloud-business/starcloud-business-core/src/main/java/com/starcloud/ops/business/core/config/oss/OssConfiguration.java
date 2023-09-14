package com.starcloud.ops.business.core.config.oss;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2022-06-22
 */
@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class OssConfiguration {

    @Bean
    public OSS ossClient(OssProperties ossProperties) {
        ClientBuilderConfiguration config = new ClientBuilderConfiguration();
        config.setSupportCname(ossProperties.isSupportCname());
        return new OSSClientBuilder()
                .build(ossProperties.getEndpoint(), ossProperties.getAccessKey(), ossProperties.getSecretKey(), config);
    }
}
