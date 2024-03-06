package com.starcloud.ops.business.app.enums.xhs.material;

import lombok.Getter;

@Getter
public enum FieldTypeEnum {

    image("image", "图片"),

    string("string", "字符串"),

    number("number","数字")
    ;

    private String typeCode;

    private String desc;

    FieldTypeEnum(String typeCode, String desc) {
        this.typeCode = typeCode;
        this.desc = desc;
    }
}
