package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppSceneEnum implements IEnumable<Integer> {

    /**
     * 我的应用场景入口
     */
    WEB_ADMIN(1, "WEB_ADMIN"),

    /**
     * 应用市场场景入口
     */
    WEB_MARKET(2, "WEB_MARKET"),


    /**
     * 分享页面场景入口
     */
    SHARE_WEB(3, "SHARE_WEB"),

    /**
     * iframe场景入口
     */
    SHARE_IFRAME(4, "SHARE_IFRAME"),


    /**
     * js页面场景入口
     */
    SHARE_JS(5, "SHARE_JS"),

    /**
     * API场景入口
     */
    SHARE_API(6, "SHARE_API"),

    /**
     * 聊天测试场景
     */
    CHAT_TEST(7,"CHAT_TEST"),

    /**
     * chat 场景
     */
    CHAT(8,"CHAT"),

    /**
     * 系统总结场景
     */

    SYSTEM_SUMMARY(8,"SYSTEM_SUMMARY"),

    /**
     * 企业微信群
     */
    WECOM_GROUP(9,"WECOM_GROUP")


    ;
    /**
     * 应用类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 应用类型说明
     */
    @Getter
    private final String label;

    /**
     * 构造函数
     *
     * @param code  枚举值
     * @param label 枚举描述
     */
    AppSceneEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
