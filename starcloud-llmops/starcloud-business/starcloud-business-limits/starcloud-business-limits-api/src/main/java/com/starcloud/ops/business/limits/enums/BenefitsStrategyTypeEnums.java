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

    //====================================支付套餐=======================================================================
    /**
     * PLUS套餐
     */
    PAY_PLUS_MONTH("PAY_PLUS_MONTH", "高级版-月付", "PLUS Package", "PL"),
    /**
     * PLUS套餐
     */
    PAY_PLUS_YEAR("PAY_PLUS_YEAR", "高级版-年付", "PLUS Package", "PL"),
    /**
     * PRO套餐
     */
    PAY_PRO_MONTH("PAY_PRO_MONTH", "团队版-月付", "PRO Package", "PR"),
    /**
     * PRO套餐
     */
    PAY_PRO_YEAR("PAY_PRO_YEAR", "高级版-年付", "PRO Package", "PR"),

    /**
     * 自定义套餐
     */
    GIFT("GIFT", "系统赠送", "System gift", "GI"),

    /**
     * 体验包
     */
    EXPERIENCE("EXPERIENCE", "体验包", "Experience Package", "EX"),

    /**
     * 公众号
     */
    WECHAT_OFFICIAL_ACCOUNTS("WECHAT_OFFICIAL_ACCOUNTS", "系统赠送-关注公众号", "System gift", "WA"),
    /**
     * 微信群
     */
    WECHAT_GROUP("WECHAT_GROUP", "系统赠送-进入官方微信群", "System gift", "VQ"),
    /**
     * 视频号
     */
    WECHAT_CHANNELS("WECHAT_CHANNELS", "系统赠送-关注官方视频号", "System gift", "CS"),
    /**
     * 抖音群
     */
    DOUYIN_GROUP("DOUYIN_GROUP", "系统赠送-关注官方抖音", "System gift", "DG"),
    /**
     * 小红书
     */
    LITTLE_RED_BOOK("LITTLE_RED_BOOK", "系统赠送-关注官方小红书账号", "System gift", "RB"),


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
