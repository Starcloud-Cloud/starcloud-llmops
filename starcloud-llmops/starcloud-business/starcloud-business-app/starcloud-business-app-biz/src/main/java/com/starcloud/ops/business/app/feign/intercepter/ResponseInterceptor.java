package com.starcloud.ops.business.app.feign.intercepter;

import feign.InvocationContext;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

//
//@Slf4j
//public class ResponseInterceptor implements feign.ResponseInterceptor {
//
//
//    @Override
//    public Object aroundDecode(InvocationContext invocationContext) throws IOException {
//
//
//        // 打印响应头信息
//        invocationContext.response().headers().forEach((key, value) -> {
//            log.info("Response Header - {}: {}", key, value);
//        });
//
//        // 继续处理响应
//        return invocationContext.proceed();
//    }
//}
