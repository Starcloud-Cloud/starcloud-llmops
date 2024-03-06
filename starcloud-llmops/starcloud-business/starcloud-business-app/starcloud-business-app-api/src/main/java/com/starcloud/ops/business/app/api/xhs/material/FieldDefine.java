package com.starcloud.ops.business.app.api.xhs.material;

import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldDefine {

    /**
     * 字段描述
     * @return
     */
    String desc();

    /**
     * 字段类型
     * @return
     */
    FieldTypeEnum type();

}
