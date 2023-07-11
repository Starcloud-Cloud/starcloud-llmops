package com.starcloud.ops.business.limits.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserLevelEnums {
    FREE("FREE","普通用户","MOFAAI_FREE"),

    PLUS("PLUS","PLUS用户","MOFAAI_PLUS"),

    PRO("PRO","PRO用户","MOFAAI_PRO"),
    ;

    private final String code;

    private final String msg;

    private final String roleCode;


    public static String getRoleCodeByLevel(UserLevelEnums level) {
        for (UserLevelEnums userLevel : UserLevelEnums.values()) {
            if (userLevel == level) {
                return userLevel.getRoleCode();
            }
        }
        // 返回FREE对应的roleCode作为默认值
        return UserLevelEnums.FREE.getRoleCode();
    }
}
