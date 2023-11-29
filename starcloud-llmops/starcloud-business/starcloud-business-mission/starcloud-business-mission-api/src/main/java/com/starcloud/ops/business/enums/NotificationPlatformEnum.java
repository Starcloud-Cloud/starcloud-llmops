package com.starcloud.ops.business.enums;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

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
}
