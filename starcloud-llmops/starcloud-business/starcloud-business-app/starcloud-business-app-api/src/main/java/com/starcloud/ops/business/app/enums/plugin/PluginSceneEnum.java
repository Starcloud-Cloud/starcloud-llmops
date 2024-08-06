package com.starcloud.ops.business.app.enums.plugin;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum PluginSceneEnum implements IEnumable<String> {

    DATA_ADDED("DATA_ADDED", "数据新增"),

    DATA_COMPLETION("DATA_COMPLETION", "数据补齐"),

    DATA_EXTRACTION("DATA_EXTRACTION", "数据提取"),

    DATA_MODIFY("DATA_MODIFY", "数据修改"),

    IMAGE_ANALYSIS("IMAGE_ANALYSIS", "图片分析");


    private String code;

    private String name;

    PluginSceneEnum(String code, String name) {

        this.code = code;

        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return name;
    }

    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(PluginSceneEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getLabel());
                    option.setValue(item.name());
                    return option;
                }).collect(Collectors.toList());
    }
}
