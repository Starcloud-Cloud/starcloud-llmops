package com.starcloud.ops.business.limits.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.units.qual.C;

@Getter
@AllArgsConstructor
public enum UserLevelEnums {

    FREE("FREE", "免费", "MOFAAI_FREE",1,1,1,1,0),

    BASIC("BASIC", "基础", "MOFAAI_BASIC",5,5,1,5,1),

    PLUS("PLUS", "高级", "MOFAAI_PLUS",20,20,10,20,3),

    PRO("PRO", "团队", "MOFAAI_PRO",-1,-1,-1,-1,10),

    RED_BOOK("RED_BOOK", "矩阵号", "MOFAAI_MEDIA",1,1,1,1,0),
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
