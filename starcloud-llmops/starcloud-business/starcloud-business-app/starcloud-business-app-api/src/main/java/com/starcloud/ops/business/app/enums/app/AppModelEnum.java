package com.starcloud.ops.business.app.enums.app;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-13
 */
public enum AppModelEnum {

    /**
     * 生成内容/图片等应用
     */
    COMPLETION(1, "生成式应用"),

    /**
     * 聊天应用
     */
    CHAT(2, "聊天式应用");

    /**
     * 应用类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 应用类型说明
     */
    @Getter
    private final String message;

    /**
     * 用 Map 将枚举在初始化时候缓存，方便后续查询
     */
    private static final Map<String, AppModelEnum> TEMPLATE_MODEL_CACHE = new ConcurrentHashMap<>();

    static {
        Arrays.stream(AppModelEnum.values()).forEach(item -> TEMPLATE_MODEL_CACHE.put(item.name(), item));
    }

    /**
     * 构造函数
     *
     * @param code    枚举值
     * @param message 枚举描述
     */
    AppModelEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 枚举缓存
     *
     * @return 枚举缓存
     */
    public static Map<String, AppModelEnum> cache() {
        return TEMPLATE_MODEL_CACHE;
    }
}
