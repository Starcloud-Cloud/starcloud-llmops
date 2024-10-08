package com.starcloud.ops.business.app.enums.xhs.material;

import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum FieldTypeEnum {

    image("image", "图片"),

    listImage("listImage", "图片"),

    string("string", "字符串输入框"),

    select("select", "下拉框"),

    weburl("weburl", "http地址"),

    listStr("listStr","字符串集合"),

    textBox("textBox", "字符串文本框"),
    ;

    private final String typeCode;

    private final String desc;

    FieldTypeEnum(String typeCode, String desc) {
        this.typeCode = typeCode;
        this.desc = desc;
    }

    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(FieldTypeEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getDesc());
                    option.setValue(item.getTypeCode());
                    return option;
                }).collect(Collectors.toList());
    }
}
