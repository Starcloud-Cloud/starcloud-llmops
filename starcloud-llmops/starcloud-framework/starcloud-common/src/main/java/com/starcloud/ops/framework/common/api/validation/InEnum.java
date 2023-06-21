package com.starcloud.ops.framework.common.api.validation;

import com.starcloud.ops.framework.common.api.enums.IEnumable;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验值是否属于指定枚举类中的值
 *
 * @author nacoyerd
 * @version 1.0.0
 * @since 2023-06-16
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = InEnumValidator.class)
public @interface InEnum {

    /**
     * 实现 IEnumable 接口的枚举类
     *
     * @return 实现 IEnumable 接口的枚举类
     */
    @SuppressWarnings("rawtypes")
    Class<? extends IEnumable> value();

    /**
     * 枚举类中的字段枚举, 默认为 name
     *
     * @return {@link EnumField}
     */
    EnumField field() default EnumField.NAME;

    String message() default "必须在指定范围 {value}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 枚举类中的字段枚举
     *
     * @author nacoyer
     * @version 1.0.0
     * @since 2023-06-16
     */
    enum EnumField {
        NAME,
        CODE,
    }
}
