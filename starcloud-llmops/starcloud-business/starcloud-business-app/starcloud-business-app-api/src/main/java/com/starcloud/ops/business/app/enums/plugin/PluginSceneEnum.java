package com.starcloud.ops.business.app.enums.plugin;

import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public enum PluginSceneEnum implements IEnumable<String> {

    DATA_ADDED("DATA_ADDED", "数据新增", "通过AI技术生成新的素材"),

    DATA_COMPLETION("DATA_COMPLETION", "数据补齐", "通过AI技术生成素材中的部分字段内容"),

    DATA_EXTRACTION("DATA_EXTRACTION", "数据提取", "通过AI技术提取素材中的部分字段内容"),

    DATA_MODIFY("DATA_MODIFY", "数据修改", "通过AI技术修改素材中的部分字段内容"),

    IMAGE_ANALYSIS("IMAGE_ANALYSIS", "图片分析", "通过AI技术提取图片中的文字信息");


    private final String code;

    private final String name;

    private final String description;

    PluginSceneEnum(String code, String name, String description) {

        this.code = code;

        this.name = name;

        this.description = description;
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
                    option.setValue(item.getCode());
                    option.setDescription(item.description);
                    return option;
                }).collect(Collectors.toList());
    }
}
