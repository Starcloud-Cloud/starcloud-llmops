package com.starcloud.ops.business.app.enums.plugin;

public enum FieldTypeEnum {

    String("String", "字符串"),

    Integer("Integer", "整数"),

    Boolean("Boolean", "布尔值"),

    Number("Number", "小数"),

    Object("Object", "对象"),

    Array_Str("Array<String>", "字符串数组"),

    Array_Int("Array<Integer>", "整数数组"),

    Array_Bool("Array<Boolean>", "布尔数组"),

    Array_Num("Array<Number>", "小数数组"),

    Array_Obj("Array<Object>", "对象数组")


//     常量 变量   ？
    ;


    private String code;

    private String name;


    FieldTypeEnum(String code, String name) {

        this.code = code;
        this.name = name;
    }
}
