package com.starcloud.ops.business.app.domain.entity.config;

/**
 * 基础配置实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-16
 */
public abstract class BaseConfigEntity {

    /**
     * 校验实体，对一写复杂逻辑的校验，可以在这里实现
     */
    abstract void validate();

}
