package com.starcloud.ops.business.app.api.xhs.material;

import com.starcloud.ops.business.app.enums.xhs.material.FieldTypeEnum;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldDefine {

    String desc();

    FieldTypeEnum type();

}
