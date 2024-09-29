package com.starcloud.ops.business.app.enums.plugin;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum OutputTypeEnum implements IEnumable<String> {

    list("list", "集合对象"),
    obj("obj", "对象"),
    ;

    private final String code;

    private final String name;


    OutputTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getLabel() {
        return name;
    }

    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(OutputTypeEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getLabel());
                    option.setValue(item.getCode());
                    return option;
                }).collect(Collectors.toList());
    }
}
