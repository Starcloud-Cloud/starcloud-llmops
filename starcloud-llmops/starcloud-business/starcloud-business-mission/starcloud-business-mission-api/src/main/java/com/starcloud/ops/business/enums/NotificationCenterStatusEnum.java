package com.starcloud.ops.business.enums;

import lombok.Getter;

@Getter
public enum NotificationCenterStatusEnum {

    init("init", "初始化"),
    published("published", "发布"),
    cancel_published("cancel_published", "取消发布"),
    ;

    private String code;

    private String desc;

    NotificationCenterStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
