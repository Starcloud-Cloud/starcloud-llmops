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
    SIGN_IN("SIGN_IN", "注册", "Sign In", "SI"),

    /**
     * 邀请注册
     */
    INVITE_TO_REGISTER("INVITE_TO_REGISTER", "邀请注册", "Invite to Register", "SN"),

    /**
     * 邀请
     */
    USER_INVITE("USER_INVITE", "邀请", "Invite", "IN"),

    /**
     * 签到
     */
    USER_ATTENDANCE("USER_ATTENDANCE", "签到", "Check In", "AT"),

    /**
     * PLUS套餐
     */
    PAY_PLUS_MONTH("PAY_PLUS_MONTH", "PLUS套餐", "PLUS Package", "PL"),

    /**
     * PLUS套餐
     */
    PAY_PLUS_YEAR("PAY_PLUS_YEAR", "PLUS套餐", "PLUS Package", "PL"),
    /**
     * PRO套餐
     */
    PAY_PRO_MONTH("PAY_PRO_MONTH", "PLUS套餐", "PRO Package", "PR"),
    /**
     * PRO套餐
     */
    PAY_PRO_YEAR("PAY_PRO_YEAR", "PLUS套餐", "PRO Package", "PR"),

    /**
     * PRO套餐
     */
    GIFT("GIFT", "赠送", "gift", "GI"),
    ;
    /**
     * code
     */
    private final String name;

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




    public static BenefitsStrategyTypeEnums getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getName().equals(code), values());
    }
}
