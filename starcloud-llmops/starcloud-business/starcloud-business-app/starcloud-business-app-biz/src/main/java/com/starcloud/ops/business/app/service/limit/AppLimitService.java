package com.starcloud.ops.business.app.service.limit;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 应用限流服务
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-28
 */
public interface AppLimitService {

    /**
     * 应用限流，应用执行限流，走系统默认限流
     *
     * @param request 请求数据
     */
    void appLimit(AppLimitRequest request);

    /**
     * 应用限流，应用执行限流，走系统默认限流
     *
     * @param request 请求数据
     * @param emitter sse
     */
    void appLimitSse(AppLimitRequest request, SseEmitter emitter);

    /**
     * 应用限流，应用市场执行限流，走系统默认限流
     *
     * @param request 请求数据
     */
    void marketLimit(AppLimitRequest request);


    /**
     * 应用限流，应用发布渠道执行限流。走用户配置限流。系统默认限流兜底
     *
     * @param request 请求数据
     */
    void channelLimit(AppLimitRequest request);
}
