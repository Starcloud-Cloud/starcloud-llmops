package com.starcloud.ops.business.limits.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

/**
 * 用户权益 - 策略类型的枚举
 * 枚举值
 *
 * @author AlanCusack
 */
@Getter
@AllArgsConstructor
public enum BenefitsTypeEnums {

    /**
     * 应用
     */
    APP("APP", "应用","app"),

    /**
     * 数据集
     */
    DATASET("DATASET", "数据集","dataset"),

    /**
     * 图片
     */
    IMAGE("IMAGE", "图片","image"),

    /**
     * 令牌
     */
    TOKEN("TOKEN", "令牌","token"),


    /**
     * 魔法豆
     */
    COMPUTATIONAL_POWER("COMPUTATIONAL_POWER", "魔法豆","computational_power"),

    /**
     * 机器人
     */
    BOT("BOT", "机器人","bot"),

    /**
     * 微信机器人
     */
    WECHAT_BOT("WECHAT_BOT", "微信机器人","wechatBot"),

    /**
     * 技能插件
     */
    SKILL_PLUGIN("SKILL_PLUGIN", "机器人技能插件","skillPlugin"),
    /**
     * 机器人文档数
     */
    BOT_DOCUMENT("BOT_DOCUMENT", "机器人文档数","botDocument"),
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
     * 数据库字段前缀
     */
    private final String englishName;


    public static BenefitsTypeEnums getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getCode().equals(code), values());
    }

    public String getDisplayName(Locale locale) {

        if (Locale.CHINA.equals(locale)) {
            return this.getChineseName();
        }
        return this.englishName;
    }
}
