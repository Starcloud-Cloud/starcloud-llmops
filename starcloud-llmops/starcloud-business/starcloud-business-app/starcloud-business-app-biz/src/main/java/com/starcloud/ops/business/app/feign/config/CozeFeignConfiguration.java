package com.starcloud.ops.business.app.feign.config;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * ClipDrop open feign 拦截器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-06
 */
@Slf4j
public class CozeFeignConfiguration {

    /**
     * ClipDrop api key
     */
    @Value("${starcloud-llm.business.app.coze.api-key}")
    private String apiKey;

    /**
     * ClipDrop 认证
     */
    private static final String AUTHORIZATION = "Authorization";

    /**
     * 拦截器，统一添加 认证 key
     *
     * @return RequestInterceptor
     */
    @Bean
    public RequestInterceptor clipDropRequestInterceptor() {
        return template -> {
            if (StringUtils.isBlank(apiKey)) {
                log.error("Coze add 'Authorization' failure, because missing Authorization api key.");
                throw ServiceExceptionUtil.exception(new ErrorCode(300300099, "Coze missing api key."));
            }
            template.header(AUTHORIZATION, "Bearer " + apiKey);
            log.info("Coze add 'Authorization' success");
        };
    }
}
