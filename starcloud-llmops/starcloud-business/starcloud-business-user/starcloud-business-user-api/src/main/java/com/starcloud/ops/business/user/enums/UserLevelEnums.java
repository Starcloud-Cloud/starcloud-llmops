package com.starcloud.ops.business.user.enums;

public enum UserLevelEnums {
    FREE("FREE","普通用户"),
    PRO("PRO","PRO用户"),
    PLUS("PLUS","PRO用户");

    private String code;
    private String msg;

    //TODO 增加角色code 关联角色
    // 1.提供查询用户等级接口 入参 用户 ID


    UserLevelEnums(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
