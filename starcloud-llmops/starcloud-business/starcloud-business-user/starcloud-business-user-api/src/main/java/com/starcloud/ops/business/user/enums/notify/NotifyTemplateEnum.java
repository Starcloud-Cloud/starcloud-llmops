package com.starcloud.ops.business.user.enums.notify;

import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public enum NotifyTemplateEnum {

    NOTIFY_PURCHASE_EXPERIENCE("NOTIFY_PURCHASE_EXPERIENCE", "2-3天内的新用户，没买过体验包.", "{username} {magicBean} "),

    NOTIFY_EXPERIENCE_EXPIRED("NOTIFY_EXPERIENCE_EXPIRED", "买过体验包，还有一天过期", " {username} {level} {expireTime}"),
    ;

    private String code;

    private String desc;

    private String templateKey;

    NotifyTemplateEnum(String code, String desc, String templateKey) {
        this.code = code;
        this.desc = desc;
        this.templateKey = templateKey;
    }

    public static List<Option> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(NotifyTemplateEnum::ordinal))
                .map(item -> {
                    Option option = new Option();
                    option.setDescription(item.getDesc());
                    option.setValue(item.getCode());
                    option.setLabel(item.templateKey);
                    return option;
                }).collect(Collectors.toList());
    }

    public static Boolean contains(String code) {
        for (NotifyTemplateEnum value : NotifyTemplateEnum.values()) {
            if (Objects.equals(value.getCode(), code)) {
                return true;
            }
        }
        return false;
    }
}
