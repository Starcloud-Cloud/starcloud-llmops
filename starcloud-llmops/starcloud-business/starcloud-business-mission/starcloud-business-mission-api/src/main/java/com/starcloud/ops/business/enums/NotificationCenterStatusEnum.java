package com.starcloud.ops.business.enums;

import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum NotificationCenterStatusEnum {

    init("init", "待发布"),
    published("published", "发布"),
    cancel_published("cancel_published", "取消发布"),
    ;

    private String code;

    private String desc;

    NotificationCenterStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取类型枚举
     *
     * @return 类型枚举
     */
    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(NotificationCenterStatusEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setLabel(item.getDesc());
                    option.setValue(item.getCode());
                    return option;
                }).collect(Collectors.toList());
    }
}
