package com.starcloud.ops.business.app.service.xhs.material.strategy;

import com.starcloud.ops.business.app.enums.xhs.material.MaterialTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-09-22
 */
@Target({ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface MaterialType {

    /**
     * 资料库类型
     *
     * @return 资料库类型枚举
     */
    String value();
}
