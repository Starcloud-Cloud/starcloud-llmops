package com.starcloud.ops.business.app.dal.redis;

/**
 * Redis Key 枚举类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
public interface RedisKeyConstants {

    /**
     * 限流配置前缀
     */
    String PUBLISH_LIMIT_RECORD_PREFIX = "PUBLISH_LIMIT_RECORD:";
}
