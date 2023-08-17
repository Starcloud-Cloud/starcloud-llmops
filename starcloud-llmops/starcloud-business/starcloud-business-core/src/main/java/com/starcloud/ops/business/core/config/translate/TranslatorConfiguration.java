package com.starcloud.ops.business.core.config.translate;

import cn.hutool.core.util.StrUtil;
import com.aliyun.alimt20181012.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
@Configuration
@EnableConfigurationProperties(TranslatorProperties.class)
public class TranslatorConfiguration {

    /**
     * 阿里云翻译客户端
     *
     * @param translatorProperties 翻译配置
     * @return 阿里云翻译客户端
     * @throws Exception 异常
     */
    @Bean
    public Client mtClient(TranslatorProperties translatorProperties) throws Exception {
        TranslatorProperties.AliyunTranslatorProperties aliyun = translatorProperties.getAliyun();
        if (aliyun == null) {
            throw new IllegalArgumentException("aliyun translator properties is null");
        }
        if (StrUtil.isBlank(aliyun.getAccessKey())) {
            throw new IllegalArgumentException("aliyun translator access key id is null");
        }
        if (StrUtil.isBlank(aliyun.getSecretKey())) {
            throw new IllegalArgumentException("aliyun translator secret key is null");
        }
        if (StrUtil.isBlank(aliyun.getEndpoint())) {
            throw new IllegalArgumentException("aliyun translator endpoint is null");
        }

        Config config = new Config()
                .setAccessKeyId(aliyun.getAccessKey())
                .setAccessKeySecret(aliyun.getSecretKey())
                .setEndpoint(aliyun.getEndpoint());
        return new Client(config);
    }

}
