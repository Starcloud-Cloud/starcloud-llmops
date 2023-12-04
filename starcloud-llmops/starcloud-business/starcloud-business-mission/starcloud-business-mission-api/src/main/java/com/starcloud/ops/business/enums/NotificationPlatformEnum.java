package com.starcloud.ops.business.enums;

import com.starcloud.ops.business.app.enums.scheme.CreativeSchemeRefersSourceEnum;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum NotificationPlatformEnum implements IEnumable<String> {

    xhs("xhs", "小红书"),
    tiktok("tiktok", "抖音"),
    other("other", "其他"),

    ;

    private String code;

    private String desc;

    NotificationPlatformEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getLabel() {
        return code;
    }

    /**
     * 获取类型枚举
     *
     * @return 类型枚举
     */
    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(NotificationPlatformEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getDesc());
                    option.setValue(item.getCode());
                    return option;
                }).collect(Collectors.toList());
    }
}
