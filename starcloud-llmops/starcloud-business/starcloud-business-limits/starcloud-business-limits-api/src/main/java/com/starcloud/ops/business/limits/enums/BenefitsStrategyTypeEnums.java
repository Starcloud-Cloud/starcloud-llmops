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
    SIGN_IN("SIGN_IN", "注册", "Sign In", "SI",1),

    /**
     * 邀请注册
     */
    INVITE_TO_REGISTER("INVITE_TO_REGISTER", "邀请注册", "Invite to Register", "SN",1),

    /**
     * 邀请
     */
    USER_INVITE("USER_INVITE", "邀请", "Invite", "IN",Integer.MAX_VALUE),

    /**
     * 签到
     */
    USER_ATTENDANCE("USER_ATTENDANCE", "签到", "Check In", "AT",1),

    /**
     * PLUS套餐
     */
    PAY_PLUS("PAY_PLUS", "PLUS套餐", "PLUS Package", "PL",Integer.MAX_VALUE),

    /**
     * PRO套餐
     */
    GIFT("PAY_PRO", "赠送", "PRO Package", "PR",Integer.MAX_VALUE),
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

    /**
     * 最大使用次数
     */
    private final int maxUsageCount;




    public static BenefitsStrategyTypeEnums getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getName().equals(code), values());
    }
}
