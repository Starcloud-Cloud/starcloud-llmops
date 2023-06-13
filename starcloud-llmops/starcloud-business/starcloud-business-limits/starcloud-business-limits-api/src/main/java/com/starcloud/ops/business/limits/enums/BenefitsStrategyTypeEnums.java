package com.starcloud.ops.business.limits.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户权益 - 策略类型的枚举
 * 枚举值
 *
 * @author AlanCusack
 */
@Getter
@AllArgsConstructor
public enum BenefitsStrategyTypeEnums {

    /**
     * 普通注册
     */
    SIGN_IN("SI0", "注册", "Sign In", "SI",1),

    /**
     * 邀请注册
     */
    INVITE_TO_REGISTER("SN1", "邀请注册", "Invite to Register", "SN",1),

    /**
     * 邀请
     */
    USER_INVITE("IN2", "邀请", "Invite", "IN",Integer.MAX_VALUE),

    /**
     * 签到
     */
    USER_ATTENDANCE("AT3", "签到", "Check In", "AT",1),

    /**
     * PLUS套餐
     */
    PAY_PLUS("PL4", "PLUS套餐", "PLUS Package", "PL",Integer.MAX_VALUE),

    /**
     * PRO套餐
     */
    PAY_PRO("PR5", "PRO套餐", "PRO Package", "PR",Integer.MAX_VALUE),
    ;
    /**
     * code
     */
    private final String code;

    /**
     * 中文名称
     */
    private final String chineseName;

    /**
     * 英文名称
     */
    private final String englishName;

    /**
     * 配置类
     */
    private final String prefix;

    /**
     * 最大使用次数
     */
    private final int maxUsageCount;




    public static BenefitsStrategyTypeEnums getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }
}
