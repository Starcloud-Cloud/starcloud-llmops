package com.starcloud.ops.business.app.translator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 翻译器类型注解，用于获取具体的翻译器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-24
 */
@Target({ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface TranslatorType {

    /**
     * 翻译器类型
     *
     * @return 翻译器类型
     */
    TranslatorTypeEnum value();
}
