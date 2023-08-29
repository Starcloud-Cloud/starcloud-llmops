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
    WEB_MARKET(2, "应用市场", "Application Market"),

    /**
     * 图片应用场景入口
     */
    WEB_IMAGE(3, "AI作图", "AI Draw"),

    /**
     * 分享页面场景入口
     */
    SHARE_WEB(4, "页面分享", "Page Share"),

    /**
     * iframe场景入口
     */
    SHARE_IFRAME(5, "IFrame分享", "IFrame Share"),

    /**
     * js页面场景入口
     */
    SHARE_JS(6, "JS分享", "JS Share"),

    /**
     * API场景入口
     */
    SHARE_API(7, "API调用", "API Call"),

    /**
     * 聊天测试场景, 调试用，现在还没用
     */
    CHAT_TEST(8, "聊天测试", "Chat Test"),

    /**
     * 企业微信群
     */
    WECOM_GROUP(9, "企业微信群", "WeCom Group"),

    /**
     * 微信公共号
     */
    MP(10, "微信公共号", "WeChat Public Account"),

    /**
     * 优化提示
     */
    OPTIMIZE_PROMPT(11, "优化提示", "Optimize Prompt"),

    /**
     * 员工广场
     */
    CHAT_MARKET(12,"员工广场","Employee Square");

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
            AppSceneEnum.WEB_IMAGE
    );

    /**
     * 应用分析场景
     */
    public static final List<AppSceneEnum> APP_ANALYSIS_SCENES = Arrays.asList(
            AppSceneEnum.WEB_ADMIN,
            AppSceneEnum.WEB_MARKET,
            AppSceneEnum.SHARE_WEB,
            AppSceneEnum.SHARE_IFRAME,
            AppSceneEnum.SHARE_JS,
            AppSceneEnum.SHARE_API
    );

    /**
     * 应用分析场景名称
     */
    public static final List<String> APP_ANALYSIS_SCENES_NAME = APP_ANALYSIS_SCENES.stream().map(AppSceneEnum::name).collect(Collectors.toList());

    /**
     * 聊天分析场景
     */
    public static final List<AppSceneEnum> CHAT_ANALYSIS_SCENES = Arrays.asList(
            AppSceneEnum.WEB_ADMIN,
            AppSceneEnum.WEB_MARKET,
            AppSceneEnum.SHARE_WEB,
            AppSceneEnum.SHARE_IFRAME,
            AppSceneEnum.SHARE_JS,
            AppSceneEnum.SHARE_API,
            AppSceneEnum.WECOM_GROUP,
            AppSceneEnum.MP,
            AppSceneEnum.CHAT_TEST
    );


    /**
     * 只获取当前登录用户态的 场景
     *
     * @return
     */
    public static Boolean inLoginUserIdScene(AppSceneEnum sceneEnum) {

        List<AppSceneEnum> loginUserScene = Arrays.asList(
                AppSceneEnum.WEB_ADMIN,
                AppSceneEnum.WEB_MARKET,
                AppSceneEnum.CHAT_TEST
        );

        return loginUserScene.contains(sceneEnum);
    }

    /**
     * 聊天分析场景名称
     */
    public static final List<String> CHAT_ANALYSIS_SCENES_NAME = CHAT_ANALYSIS_SCENES.stream().map(AppSceneEnum::name).collect(Collectors.toList());

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
