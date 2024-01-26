package com.starcloud.ops.business.user.enums.notify;

import com.starcloud.ops.business.user.enums.dept.UserDeptRoleEnum;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum NotifyMediaEnum {
//    sms(1,"手机短信"),
    wx_mp(2,"微信公共号"),

    ;


    private Integer type;

    private String desc;

    NotifyMediaEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(NotifyMediaEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setDescription(item.getDesc());
                    option.setValue(item.getType());
                    option.setLabel(item.name());
                    return option;
                }).collect(Collectors.toList());
    }
}
