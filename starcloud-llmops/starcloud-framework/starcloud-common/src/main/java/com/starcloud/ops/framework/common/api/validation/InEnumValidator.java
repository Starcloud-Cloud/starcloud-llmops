package com.starcloud.ops.framework.common.api.validation;

import com.starcloud.ops.framework.common.api.enums.IEnumable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-16
 */
public class InEnumValidator implements ConstraintValidator<InEnum, Object> {

    @SuppressWarnings("rawtypes")
    private Class<? extends IEnumable> clazz;

    private InEnum.EnumField field;

    @Override
    public void initialize(InEnum annotation) {
        clazz = annotation.value();
        field = annotation.field();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 如果值为空，直接返回 true, 交给 @NotNull 或者 @NotBlank 校验
        if (Objects.isNull(value)) {
            return true;
        }

        // 如果不是枚举，直接返回 false
        if (!clazz.isEnum()) {
            return false;
        }

        // 遍历枚举值，如果有一个匹配，返回 true
        IEnumable[] values = clazz.getEnumConstants();

        // 如果枚举值为空，直接返回 false
        if (Objects.isNull(values)) {
            return false;
        }

        // 遍历枚举值，如果有一个匹配，返回 true
        boolean result = Arrays.stream(values).anyMatch(enumValue -> {
            Object compareValue = (field == InEnum.EnumField.CODE) ? enumValue.getCode() : enumValue.toString();
            return Objects.equals(value, compareValue.toString());
        });

        if (!result) {
            // 校验不通过，自定义提示语句（因为，注解上的 value 是枚举类，无法获得枚举类的实际值）
            String message = context.getDefaultConstraintMessageTemplate()
                    .replace("{value}", value.toString())
                    .replace("{values}", (field == InEnum.EnumField.CODE) ?
                            Arrays.stream(values).map(IEnumable::getCode).collect(Collectors.toList()).toString() : Arrays.toString(values)
                    );
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return result;
    }

}

