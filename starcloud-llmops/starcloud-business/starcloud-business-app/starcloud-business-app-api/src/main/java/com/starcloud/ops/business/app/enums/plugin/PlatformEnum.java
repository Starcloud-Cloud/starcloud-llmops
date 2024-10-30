package com.starcloud.ops.business.app.enums.plugin;

import com.starcloud.ops.business.app.enums.xhs.scheme.CreativeSchemeRefersSourceEnum;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum PlatformEnum implements IEnumable<String> {

    coze("coze", "扣子机器人"),

    coze_workflow("coze_workflow", "扣子工作流"),

    app_market("app_market", "应用市场");


    private final String code;

    private final String name;


    PlatformEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getLabel() {
        return name;
    }


    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(PlatformEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getLabel());
                    option.setValue(item.name());
                    return option;
                }).collect(Collectors.toList());
    }
}
