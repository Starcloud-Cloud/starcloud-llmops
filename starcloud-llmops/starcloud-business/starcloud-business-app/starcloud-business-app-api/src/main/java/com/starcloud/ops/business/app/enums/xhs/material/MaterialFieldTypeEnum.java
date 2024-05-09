package com.starcloud.ops.business.app.enums.xhs.material;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum MaterialFieldTypeEnum implements IEnumable<String> {

    image("image", "图片"),

    string("string", "字符串输入框"),

    textBox("textBox", "字符串文本框"),

    document("document", "文档路径"),


    ;

    private final String typeCode;

    private final String desc;

    MaterialFieldTypeEnum(String typeCode, String desc) {
        this.typeCode = typeCode;
        this.desc = desc;
    }

    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(MaterialFieldTypeEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getDesc());
                    option.setValue(item.getTypeCode());
                    return option;
                }).collect(Collectors.toList());
    }

    @Override
    public String getCode() {
        return this.typeCode;
    }

    @Override
    public String getLabel() {
        return this.desc;
    }
}
