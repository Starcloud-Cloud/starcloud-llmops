package com.starcloud.ops.business.limits.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.units.qual.C;

@Getter
@AllArgsConstructor
public enum UserLevelEnums {

    FREE("FREE", "普通用户", "MOFAAI_FREE",2,2,1,2,0),

    PLUS("PLUS", "PLUS用户", "MOFAAI_PLUS",20,20,10,20,3),

    PRO("PRO", "PRO用户", "MOFAAI_PRO",-1,-1,-1,-1,10),
    ;

    private final String code;

    private final String msg;

    private final String roleCode;

    private final Integer app;

    private final Integer bot;

    private final Integer wechatBot;

    private final Integer botDocument;

    private final Integer skillPlugin;


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
