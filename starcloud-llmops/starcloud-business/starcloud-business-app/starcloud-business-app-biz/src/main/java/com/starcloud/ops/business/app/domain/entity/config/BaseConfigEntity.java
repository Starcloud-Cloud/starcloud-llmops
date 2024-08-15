package com.starcloud.ops.business.app.domain.entity.config;

import com.starcloud.ops.business.app.enums.ValidateTypeEnum;

/**
 * 基础配置实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-16
 */
public abstract class BaseConfigEntity {

    public void init() {

    }

    public abstract void validate(ValidateTypeEnum validateType);


}
