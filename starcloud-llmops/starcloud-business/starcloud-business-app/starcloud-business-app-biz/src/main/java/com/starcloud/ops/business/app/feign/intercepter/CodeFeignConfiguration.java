package com.starcloud.ops.business.app.feign.intercepter;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import feign.InvocationContext;
import feign.RequestInterceptor;
import feign.ResponseInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@Slf4j
public class CodeFeignConfiguration {

    /**
     * stability open feign 拦截器
     *
     * @return RequestTemplate
     */
    @Bean
    public ResponseInterceptor responseInterceptor() {

        return invocationContext -> {

            // 打印响应头信息
            invocationContext.response().headers().forEach((key, value) -> {
                log.info("Response Header - {}: {}", key, value);
            });

            // 继续处理响应
            return invocationContext.proceed();

        };

    }

}
