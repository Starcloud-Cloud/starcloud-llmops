package com.starcloud.ops.business.app.service.limit;

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
     * @param appUid 应用唯一标识
     */
    void appLimit(String appUid);

    /**
     * 应用限流，应用市场执行限流，走系统默认限流
     *
     * @param marketUid 应用唯一标识
     */
    void marketLimit(String marketUid);


    /**
     * 应用限流，应用发布渠道执行限流。走用户配置限流。系统默认限流兜底
     *
     * @param mediumUid 应用唯一标识
     */
    void channelLimit(String mediumUid);
}
