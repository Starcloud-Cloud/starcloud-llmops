package com.starcloud.ops.business.app.api.app.annotation.variable;


import com.starcloud.ops.business.app.enums.app.AppVariableStyleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 临时兼容方案，让jsonSchema 支持以前的 variableItem 定义
 * @author df007df
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface VariableItem {

//    /**
//     * 类型
//     * @see AppVariableTypeEnum
//     * @return
//     */
//    String type() default "TEXT";
//

    /**
     * 渲染样式
     * @see AppVariableStyleEnum
     * @return
     */
    String style() default "INPUT";



    /**
     * 变量是否为点位
     * @return
     */
    boolean setIsPoint() default false;


    /**
     * 变量是否显示
     */
    boolean isShow() default true;


    /**
     * 选项
     * @return
     */
    VariableItemOption[] options() default {};


}
