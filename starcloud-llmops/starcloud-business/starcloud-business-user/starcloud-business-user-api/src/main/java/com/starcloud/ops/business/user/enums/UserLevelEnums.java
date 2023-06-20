package com.starcloud.ops.business.user.enums;

public enum UserLevelEnums {
    FREE("FREE","普通用户"),
    PRO("PRO","PRO用户"),
    PLUS("PLUS","PRO用户");

    private String code;
    private String msg;

    UserLevelEnums(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
