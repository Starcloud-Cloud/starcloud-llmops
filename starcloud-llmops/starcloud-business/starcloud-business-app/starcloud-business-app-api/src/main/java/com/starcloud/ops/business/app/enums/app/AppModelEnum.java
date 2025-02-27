package com.starcloud.ops.business.app.enums.app;

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
public enum AppModelEnum implements IEnumable<Integer> {

    /**
     * 生成内容/图片等应用
     */
    COMPLETION(1, "生成", "Completion"),

    /**
     * 聊天应用
     */
    CHAT(2, "聊天", "Chat"),

    /**
     * 基础图片生成模式
     */
    IMAGE(3, "生成图片", "Image"),

    VIDEO(4, "视频生成", "Video");

    /**
     * 应用类型Code
     */
    private final Integer code;

    /**
     * 应用类型说明
     */
    private final String label;

    /**
     * 应用类型英文说明
     */
    private final String labelEn;

    /**
     * 基础应用模型
     */
    public static final List<AppModelEnum> BASE_APP_MODEL = Arrays.asList(COMPLETION, CHAT);

    public static final List<String> BASE_APP_MODEL_NAME = Arrays.asList(COMPLETION.name(), CHAT.name());

    /**
     * 构造函数
     *
     * @param code  枚举值
     * @param label 枚举描述
     */
    AppModelEnum(Integer code, String label, String labelEn) {
        this.code = code;
        this.label = label;
        this.labelEn = labelEn;
    }

    /**
     * 根据Name获取枚举
     */
    public static AppModelEnum getByName(String name) {
        return Arrays.stream(values())
                .filter(item -> item.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据 code 获得枚举
     *
     * @return 枚举
     */
    public static List<Option> getOptions() {
        return Arrays.stream(values())
                .map(item -> Option.of(item.name(), item.getLabel(), item.getLabelEn()))
                .collect(Collectors.toList());
    }

}
