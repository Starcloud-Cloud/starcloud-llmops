package com.starcloud.ops.business.app.feign.config;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Stability 配置类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-06
 */
@Slf4j
public class StabilityFeignConfiguration {

    /**
     * Stability api key
     */
    @Value("${starcloud-llm.business.app.image.stability.api-key}")
    private String apiKey;

    /**
     * ClipDrop 认证
     */
    private static final String AUTHORIZATION = "Authorization";

    /**
     * stability open feign 拦截器
     *
     * @return RequestTemplate
     */
    @Bean
    public RequestInterceptor stabilityRequestInterceptor() {
        return template -> {
            if (StringUtils.isBlank(apiKey)) {
                log.error("Stability add 'Authorization' failure, because missing stability api key.");
                throw ServiceExceptionUtil.exception(new ErrorCode(300300099, "Stability missing api key."));
            }
            template.header(AUTHORIZATION, apiKey);
            log.info("Stability add 'Authorization' success");
        };
    }

}
