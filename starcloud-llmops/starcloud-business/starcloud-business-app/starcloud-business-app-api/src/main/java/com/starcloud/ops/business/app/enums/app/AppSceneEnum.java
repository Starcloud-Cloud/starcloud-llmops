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
     * 聊天调试场景
     */
    CHAT_TEST(2, "聊天调试", "Chat Test"),

    /**
     * 应用市场场景入口
     */
    WEB_MARKET(3, "应用市场", "Application Market"),

    /**
     * 员工广场
     */
    CHAT_MARKET(4, "员工广场", "Employee Square"),

    /**
     * 图片应用场景入口
     */
    WEB_IMAGE(5, "AI自由绘图", "AI Draw"),

    /**
     * 图片高清放大
     */
    IMAGE_UPSCALING(6, "图片高清放大", "Image Upscaling"),

    /**
     * 图片去除背景
     */
    IMAGE_REMOVE_BACKGROUND(7, "图片去背景", "Image Remove Background"),

    /**
     * 图片替换背景
     */
    IMAGE_REPLACE_BACKGROUND(8, "图片替换背景", "Image Replace Background"),

    /**
     * 图片去文字
     */
    IMAGE_REMOVE_TEXT(9, "图片去文字", "Image Remove Text"),

    /**
     * 草图生成图片
     */
    IMAGE_SKETCH(10, "草图生成图片", "Sketch To Image"),

    /**
     * 图片变体
     */
    IMAGE_VARIANTS(11, "图片裂变", "Image Variants"),

    /**
     * 优化提示
     */
    OPTIMIZE_PROMPT(12, "优化提示词", "Optimize Prompt"),

    /**
     * Listing生成
     */
    LISTING_GENERATE(13, "Listing生成", "Listing Generate"),

    /**
     * 小红书文案生成
     */
    XHS_WRITING(14, "小红书文案生成", "小红书"),

    /**
     * 分享页面场景入口
     */
    SHARE_WEB(15, "页面分享", "Page Share"),

    /**
     * iframe场景入口
     */
    SHARE_IFRAME(16, "IFrame分享", "IFrame Share"),

    /**
     * js页面场景入口
     */
    SHARE_JS(17, "JS分享", "JS Share"),

    /**
     * API场景入口
     */
    SHARE_API(18, "API调用", "API Call"),

    /**
     * 企业微信群
     */
    WECOM_GROUP(19, "企业微信群", "WeCom Group"),

    /**
     * 微信公共号
     */
    MP(20, "微信公共号", "WeChat Public Account"),


    APP_TEST(21, "应用测试", "App Test")
    ;

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

    public static final List<AppSceneEnum> ADMIN_SCENES = Arrays.asList(
            WEB_ADMIN,
            CHAT_TEST,
            WEB_MARKET,
            CHAT_MARKET,
            WEB_IMAGE,
            IMAGE_UPSCALING,
            IMAGE_REMOVE_BACKGROUND,
            IMAGE_REPLACE_BACKGROUND,
            IMAGE_REMOVE_TEXT,
            IMAGE_SKETCH,
            IMAGE_VARIANTS,

            SHARE_WEB,
            SHARE_IFRAME,
            SHARE_JS,
            SHARE_API,
            MP,
            WECOM_GROUP,
            OPTIMIZE_PROMPT,
            LISTING_GENERATE,
            XHS_WRITING,
            APP_TEST

    );

    /**
     * 生成记录基础场景
     */
    public static final List<AppSceneEnum> GENERATE_RECORD_BASE_SCENES = Arrays.asList(
            WEB_ADMIN,
            CHAT_TEST,
            WEB_MARKET,
            CHAT_MARKET,
            WEB_IMAGE,
            IMAGE_UPSCALING,
            IMAGE_REMOVE_BACKGROUND,
            IMAGE_REPLACE_BACKGROUND,
            IMAGE_REMOVE_TEXT,
            IMAGE_SKETCH,
            IMAGE_VARIANTS,
            XHS_WRITING,
            APP_TEST
    );

    /**
     * 应用分析场景
     */
    public static final List<AppSceneEnum> APP_ANALYSIS_SCENES = Arrays.asList(
            WEB_ADMIN,
            WEB_MARKET,
            SHARE_WEB,
            SHARE_IFRAME,
            SHARE_JS,
            SHARE_API,
            XHS_WRITING,
            APP_TEST
    );

    /**
     * 是否是应用分析场景
     *
     * @param scene 场景
     * @return 是否是应用分析场景
     */
    public static Boolean isAppAnalysisScene(AppSceneEnum scene) {
        return APP_ANALYSIS_SCENES.contains(scene);
    }

    /**
     * 聊天分析场景
     */
    public static final List<AppSceneEnum> CHAT_ANALYSIS_SCENES = Arrays.asList(
            CHAT_TEST,
            CHAT_MARKET,
            SHARE_WEB,
            SHARE_IFRAME,
            SHARE_JS,
            SHARE_API,
            MP,
            WECOM_GROUP
    );

    /**
     * 是否是聊天分析场景
     *
     * @param scene 场景
     * @return 是否是聊天分析场景
     */
    public static Boolean isChatAnalysisScene(AppSceneEnum scene) {
        return CHAT_ANALYSIS_SCENES.contains(scene);
    }

    /**
     * 图片支持的场景
     */
    public static final List<String> SUPPORT_IMAGE_SCENE = Arrays.asList(
            WEB_IMAGE.name(),
            IMAGE_UPSCALING.name(),
            IMAGE_REMOVE_BACKGROUND.name(),
            IMAGE_REPLACE_BACKGROUND.name(),
            IMAGE_REMOVE_TEXT.name(),
            IMAGE_SKETCH.name(),
            IMAGE_VARIANTS.name()
    );

    /**
     * 应用市场场景
     */
    public static final List<AppSceneEnum> MARKET_SCENES = Arrays.asList(
            WEB_MARKET,
            OPTIMIZE_PROMPT,
            LISTING_GENERATE,
            XHS_WRITING,
            CHAT_MARKET,
            APP_TEST
    );

    /**
     * 是否是应用市场场景
     *
     * @param scene 场景
     * @return 是否是应用市场场景
     */
    public static Boolean isMarketScene(AppSceneEnum scene) {
        return MARKET_SCENES.contains(scene);
    }

    /**
     * 登录用户态的场景
     */
    public static final List<AppSceneEnum> LOGIN_USER_SCENE = Arrays.asList(
            WEB_ADMIN,
            WEB_MARKET,
            CHAT_MARKET,
            CHAT_TEST,
            OPTIMIZE_PROMPT,
            LISTING_GENERATE,
            //XHS_WRITING,
            WEB_IMAGE,
            IMAGE_UPSCALING,
            IMAGE_REMOVE_BACKGROUND,
            IMAGE_REPLACE_BACKGROUND,
            IMAGE_REMOVE_TEXT,
            IMAGE_SKETCH,
            IMAGE_VARIANTS

    );

    /**
     * 只获取当前登录用户态的 场景
     *
     * @return 场景列表
     */
    public static Boolean inLoginUserIdScene(AppSceneEnum sceneEnum) {
        return LOGIN_USER_SCENE.contains(sceneEnum);
    }

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

    /**
     * 根据Name获取枚举
     */
    public static AppSceneEnum getByName(String name) {
        return Arrays.stream(values())
                .filter(item -> item.name().equals(name))
                .findFirst()
                .orElse(null);
    }


}
