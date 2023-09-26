package com.starcloud.ops.business.app.service.image.strategy;

import com.starcloud.ops.business.app.enums.app.AppSceneEnum;

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
public @interface ImageScene {

    /**
     * 图片场景
     *
     * @return 图片场景
     */
    AppSceneEnum value();
}
