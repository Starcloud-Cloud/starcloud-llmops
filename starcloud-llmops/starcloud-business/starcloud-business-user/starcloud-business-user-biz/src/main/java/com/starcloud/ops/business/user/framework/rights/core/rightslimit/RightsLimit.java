package com.starcloud.ops.business.user.framework.rights.core.rightslimit;

import com.starcloud.ops.business.user.enums.LevelRightsLimitEnums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权益请求限制
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RightsLimit {
    /**
     * 权益类型
     *
     * @return 权益类型
     */
    // @InEnum(LevelRightsLimitEnums.class)
    LevelRightsLimitEnums value();


    /**
     * 提示信息 -可以自定义
     *
     * @return 提示信息
     */
    String info() default "今日权益已经使用完，请升级或明天再试！";

}
