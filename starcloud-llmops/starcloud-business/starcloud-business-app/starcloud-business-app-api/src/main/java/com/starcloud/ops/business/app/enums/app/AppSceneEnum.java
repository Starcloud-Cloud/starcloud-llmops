package com.starcloud.ops.business.app.enums.app;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
@Getter
public enum AppSceneEnum implements IEnumable<Integer> {

    /**
     * 我的应用场景入口
     */
    WEB_ADMIN(1, "创作中心", "Creative Center"),

    /**
     * 应用市场场景入口
     */
<<<<<<< HEAD
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
     * 聊天测试场景, 调试用，现在还没用
     */
    CHAT_TEST(7, "CHAT_TEST"),
=======
    WEB_MARKET(2, "应用市场", "Application Market"),
>>>>>>> 5303c06395c7f3367d126c874d3899042da43740

    /**
     * 图片场景
     */
    IMAGE(3, "AI作图", "AI Drawing"),

    /**
     * chat 场景
     */
    CHAT(2, "聊天", "Chat"),

    /**
     * 聊天测试场景
     */
    CHAT_TEST(5, "聊天测试", "Chat Test"),

    /**
     * 分享页面场景入口
     */
    SHARE_WEB(6, "页面分享", "Page Share"),

    /**
     * iframe场景入口
     */
    SHARE_IFRAME(7, "Iframe嵌入", "Iframe Embed"),

    /**
     * js页面场景入口
     */
    SHARE_JS(8, "JS嵌入", "JS Embed"),

    /**
     * API场景入口
     */
    SHARE_API(9, "API调用", "API Call"),

    /**
     * 企业微信群
     */
    WECOM_GROUP(10, "企业微信群", "WeCom Group"),

    /**
     * 系统总结场景
     */
<<<<<<< HEAD
    SYSTEM_SUMMARY(10, "SYSTEM_SUMMARY"),

    /**
     * 企业微信群
     */
    WECOM_GROUP(11, "WECOM_GROUP"),

    /**
     * 微信公共号
     */
    MP(12, "MP");
=======

    SYSTEM_SUMMARY(11, "系统总结", "System Summary");
>>>>>>> 5303c06395c7f3367d126c874d3899042da43740

    /**
     * 应用类型Code
     */
    private final Integer code;

    /**
     * 应用类型说明
     */
    private final String label;

    /**
     * 应用类型英文
     */
    private final String labelEn;

    /**
     * 生成记录基础场景
     */
    public static final List<AppSceneEnum> GENERATE_RECORD_BASE_SCENES = Arrays.asList(
            AppSceneEnum.WEB_ADMIN,
            AppSceneEnum.WEB_MARKET,
            AppSceneEnum.IMAGE
    );

    /**
     * 应用分析场景
     */
    public static final List<AppSceneEnum> APP_ANALYSIS_SCENES = Arrays.asList(
            AppSceneEnum.WEB_ADMIN,
            AppSceneEnum.SHARE_WEB,
            AppSceneEnum.SHARE_IFRAME,
            AppSceneEnum.SHARE_JS,
            AppSceneEnum.SHARE_API
    );

    /**
     * 聊天分析场景
     */
    public static final List<AppSceneEnum> CHAT_ANALYSIS_SCENES = Arrays.asList(
            AppSceneEnum.WEB_ADMIN,
            AppSceneEnum.CHAT,
            AppSceneEnum.CHAT_TEST,
            AppSceneEnum.SHARE_WEB,
            AppSceneEnum.SHARE_IFRAME,
            AppSceneEnum.SHARE_JS,
            AppSceneEnum.SHARE_API,
            AppSceneEnum.WECOM_GROUP
    );

    /**
     * 构造函数
     *
     * @param code  枚举值
     * @param label 枚举描述
     */
    AppSceneEnum(Integer code, String label, String labelEn) {
        this.code = code;
        this.label = label;
        this.labelEn = labelEn;
    }

    /**
     * 获取 Option 列表
     *
     * @return Option 列表
     */
    public static List<Option> getOptions() {
        return Arrays.stream(values())
                .map(item -> Option.of(item.name(), item.getLabel(), item.getLabelEn()))
                .collect(Collectors.toList());
    }

    /**
     * 获取 Option 列表
     *
     * @param sceneEnumList 枚举列表
     * @return Option 列表
     */
    public static List<Option> getOptions(List<AppSceneEnum> sceneEnumList) {
        return CollectionUtil.emptyIfNull(sceneEnumList).stream()
                .map(item -> Option.of(item.name(), item.getLabel(), item.getLabelEn()))
                .collect(Collectors.toList());
    }


}
