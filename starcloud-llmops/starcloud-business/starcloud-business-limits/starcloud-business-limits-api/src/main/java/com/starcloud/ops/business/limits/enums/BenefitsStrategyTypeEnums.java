package com.starcloud.ops.business.limits.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

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
    SIGN_IN("SIGN_IN", "注册", "Sign In", "SI", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),

    /**
     * 邀请注册
     */
    INVITE_TO_REGISTER("INVITE_TO_REGISTER", "邀请注册", "Invite to Register", "SN", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),

    /**
     * 邀请
     */
    USER_INVITE("USER_INVITE", "邀请", "Invite", "IN", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),

    /**
     * 签到
     */
    USER_ATTENDANCE("USER_ATTENDANCE", "签到", "Check In", "AT", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),


    /**
     * 自定义套餐
     */
    GIFT("GIFT", "系统赠送", "System gift", "GI", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),

    /**
     * 体验包
     */
    EXPERIENCE("EXPERIENCE", "体验包", "Experience Package", "EX", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),

    /**
     * 公众号
     */
    WECHAT_OFFICIAL_ACCOUNTS("WECHAT_OFFICIAL_ACCOUNTS", "系统赠送-关注公众号", "System gift", "WA", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),
    /**
     * 微信群
     */
    WECHAT_GROUP("WECHAT_GROUP", "系统赠送-进入官方微信群", "System gift", "VQ", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),
    /**
     * 视频号
     */
    WECHAT_CHANNELS("WECHAT_CHANNELS", "系统赠送-关注官方视频号", "System gift", "CS", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),
    /**
     * 抖音群
     */
    DOUYIN_GROUP("DOUYIN_GROUP", "系统赠送-关注官方抖音", "System gift", "DG", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),
    /**
     * 小红书
     */
    LITTLE_RED_BOOK("LITTLE_RED_BOOK", "系统赠送-关注官方小红书账号", "System gift", "RB", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),


    /**
     * 多次邀请
     */
    USER_INVITE_REPEAT("USER_INVITE_REPEAT", "系统赠送-邀请达人礼包", "Invite", "IR", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),

    //====================================支付套餐=======================================================================

    /**
     * BASIC套餐
     */
    PAY_BASIC_MONTH("PAY_BASIC_MONTH", "魔法ai-基础版-年付", "PLUS Package", "PB", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),
    /**
     * BASIC套餐
     */
    PAY_BASIC_YEAR("PAY_BASIC_YEAR", "魔法ai-基础版-年付", "PLUS Package", "PB", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),
    /**
     * PLUS套餐
     */
    PAY_PLUS_MONTH("PAY_PLUS_MONTH", "魔法ai-高级版-月付", "PLUS Package", "PL", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),
    /**
     * PLUS套餐
     */
    PAY_PLUS_YEAR("PAY_PLUS_YEAR", "魔法ai-高级版-年付", "PLUS Package", "PL", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),
    /**
     * PRO套餐
     */
    PAY_PRO_MONTH("PAY_PRO_MONTH", "魔法ai-团队版-月付", "PRO Package", "PR", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),
    /**
     * PRO套餐
     */
    PAY_PRO_YEAR("PAY_PRO_YEAR", "魔法ai-高级版-年付", "PRO Package", "PR", BenefitsTypesEnums.BASIC_BENEFITS, 0.00, null),

    /**
     *
     */
    DIRECT_DISCOUNT_NEW_USER("DIRECT_DISCOUNT_NEW_USER", "新用户专享-10 元", "direct discount10", "D0", BenefitsTypesEnums.PAY_BENEFITS, 10.00, BenefitsDiscountTypeEnums.DIRECT_DISCOUNT),
    DIRECT_DISCOUNT_10("DIRECT_DISCOUNT_10", "直接抵扣券-10 元", "direct discount10", "D1", BenefitsTypesEnums.PAY_BENEFITS, 10.00, BenefitsDiscountTypeEnums.DIRECT_DISCOUNT),
    DIRECT_DISCOUNT_50("DIRECT_DISCOUNT_50", "直接抵扣券-50 元", "direct discount50", "D2", BenefitsTypesEnums.PAY_BENEFITS, 50.00, BenefitsDiscountTypeEnums.DIRECT_DISCOUNT),
    DIRECT_DISCOUNT_100("DIRECT_DISCOUNT_100", "直接抵扣券-100 元", "direct discount100", "D3", BenefitsTypesEnums.PAY_BENEFITS, 100.00, BenefitsDiscountTypeEnums.DIRECT_DISCOUNT),
    DIRECT_DISCOUNT_200("DIRECT_DISCOUNT_200", "直接抵扣券-200 元", "DirectDiscount200", "D4", BenefitsTypesEnums.PAY_BENEFITS, 200.00, BenefitsDiscountTypeEnums.DIRECT_DISCOUNT),
    DIRECT_DISCOUNT_300("DIRECT_DISCOUNT_300", "直接抵扣券-300 元", "DirectDiscount300", "D5", BenefitsTypesEnums.PAY_BENEFITS, 300.00, BenefitsDiscountTypeEnums.DIRECT_DISCOUNT),
    DIRECT_DISCOUNT_400("DIRECT_DISCOUNT_400", "直接抵扣券-400 元", "DirectDiscount400", "D6", BenefitsTypesEnums.PAY_BENEFITS, 400.00, BenefitsDiscountTypeEnums.DIRECT_DISCOUNT),
    DIRECT_DISCOUNT_500("DIRECT_DISCOUNT_500", "直接抵扣券-500 元", "DirectDiscount500", "D7", BenefitsTypesEnums.PAY_BENEFITS, 500.00, BenefitsDiscountTypeEnums.DIRECT_DISCOUNT),
    DIRECT_DISCOUNT_1000("DIRECT_DISCOUNT_1000", "直接抵扣券-600 元", "DirectDiscount1000", "D8", BenefitsTypesEnums.PAY_BENEFITS, 1000.00, BenefitsDiscountTypeEnums.DIRECT_DISCOUNT),
    DIRECT_DISCOUNT_49("DIRECT_DISCOUNT_49", "直接抵扣券-49.1 元", "DirectDiscount49.1", "D9", BenefitsTypesEnums.PAY_BENEFITS, 49.10, BenefitsDiscountTypeEnums.DIRECT_DISCOUNT),


    PERCENTAGE_DISCOUNT_NEW_USER("PERCENTAGE_DISCOUNT_NEW_USER", "新用户专享-10%", "PercentageDiscount10", "P0", BenefitsTypesEnums.PAY_BENEFITS, 0.10, BenefitsDiscountTypeEnums.PERCENTAGE_DISCOUNT),
    PERCENTAGE_DISCOUNT_10("PERCENTAGE_DISCOUNT_10", "百分比优惠券-10%", "PercentageDiscount10", "P1", BenefitsTypesEnums.PAY_BENEFITS, 0.10, BenefitsDiscountTypeEnums.PERCENTAGE_DISCOUNT),
    PERCENTAGE_DISCOUNT_20("PERCENTAGE_DISCOUNT_20", "百分比优惠券-20%", "PercentageDiscount20", "P2", BenefitsTypesEnums.PAY_BENEFITS, 0.20, BenefitsDiscountTypeEnums.PERCENTAGE_DISCOUNT),
    PERCENTAGE_DISCOUNT_30("PERCENTAGE_DISCOUNT_30", "百分比优惠券-30%", "PercentageDiscount30", "P3", BenefitsTypesEnums.PAY_BENEFITS, 0.30, BenefitsDiscountTypeEnums.PERCENTAGE_DISCOUNT),
    PERCENTAGE_DISCOUNT_40("PERCENTAGE_DISCOUNT_40", "百分比优惠券-40%", "PercentageDiscount40", "P4", BenefitsTypesEnums.PAY_BENEFITS, 0.40, BenefitsDiscountTypeEnums.PERCENTAGE_DISCOUNT),
    PERCENTAGE_DISCOUNT_50("PERCENTAGE_DISCOUNT_50", "百分比优惠券-50%", "PercentageDiscount50", "P5", BenefitsTypesEnums.PAY_BENEFITS, 0.50, BenefitsDiscountTypeEnums.PERCENTAGE_DISCOUNT),
    PERCENTAGE_DISCOUNT_60("PERCENTAGE_DISCOUNT_60", "百分比优惠券-60%", "PercentageDiscount60", "P6", BenefitsTypesEnums.PAY_BENEFITS, 0.60, BenefitsDiscountTypeEnums.PERCENTAGE_DISCOUNT),
    PERCENTAGE_DISCOUNT_70("PERCENTAGE_DISCOUNT_70", "百分比优惠券-70%", "PercentageDiscount70", "P7", BenefitsTypesEnums.PAY_BENEFITS, 0.70, BenefitsDiscountTypeEnums.PERCENTAGE_DISCOUNT),
    PERCENTAGE_DISCOUNT_80("PERCENTAGE_DISCOUNT_80", "百分比优惠券-80%", "PercentageDiscount80", "P8", BenefitsTypesEnums.PAY_BENEFITS, 0.80, BenefitsDiscountTypeEnums.PERCENTAGE_DISCOUNT),
    PERCENTAGE_DISCOUNT_90("PERCENTAGE_DISCOUNT_90", "百分比优惠券-90%", "PercentageDiscount90", "P9", BenefitsTypesEnums.PAY_BENEFITS, 0.90, BenefitsDiscountTypeEnums.PERCENTAGE_DISCOUNT),

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
     * 前缀
     */
    private final String prefix;

    /**
     * 配置类
     */
    private final BenefitsTypesEnums typesEnums;


    private final Double discountNums;

    private final BenefitsDiscountTypeEnums discountTypeEnums;


    public static BenefitsStrategyTypeEnums getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getName().equals(code), values());
    }

    public static List<BenefitsStrategyTypeEnums> getDataByTypes(BenefitsTypesEnums typesEnums) {
        List<BenefitsStrategyTypeEnums> strategyTypeEnumsList = new ArrayList<>();

        for (BenefitsStrategyTypeEnums benefitsStrategyTypeEnumsS : BenefitsStrategyTypeEnums.values()) {
            if (benefitsStrategyTypeEnumsS.getTypesEnums().name().equals(typesEnums.name())) {
                strategyTypeEnumsList.add(benefitsStrategyTypeEnumsS);
            }
        }
        return strategyTypeEnumsList;
    }
}
