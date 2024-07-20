package com.starcloud.ops.business.app.api.app.annotation.variable;


import com.starcloud.ops.business.app.enums.app.AppVariableTypeEnum;
import com.starcloud.ops.framework.common.api.dto.Option;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * 临时兼容方案，让jsonSchema 支持以前的 VariableItemOption 定义
 * @author df007df
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface VariableItemOption {

    /**
     * 显示
     *
     */
    String label() default "";

    /**
     * 值
     *
     */
    String value() default "";


    /**
     * 描述
     */
    String description() default "";

    /**
     * 权限
     */
    String permissions();


}
