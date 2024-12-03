package com.starcloud.ops.business.app.enums.plugin;


import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum ProcessMannerEnum implements IEnumable<String> {
    riskPinyin("riskPinyin", "违禁词转拼音"),
    topPinyin("topPinyin", "禁用词转拼音"),
    lowPinyin("lowPinyin", "敏感词转拼音"),
    riskEmpty("riskEmpty", "删除违禁词"),
    topEmpty("topEmpty", "删除禁用词"),
    lowEmpty("lowEmpty", "删除敏感词")
    ;


    private final String code;

    private final String name;

    ProcessMannerEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.name;
    }

    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(ProcessMannerEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getLabel());
                    option.setValue(item.getCode());
                    return option;
                }).collect(Collectors.toList());
    }
}
