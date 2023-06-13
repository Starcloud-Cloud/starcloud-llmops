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
     * 聊天应用
     */
    CHAT(1, "聊天应用"),

    /**
     * 生成内容/图片等应用
     */
    COMPLETION(2, "生成");

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
     * @param code    应用类型 Code
     * @param message 应用类型说明
     */
    AppModelEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据枚举名称获取枚举Code
     *
     * @param name 枚举名称
     * @return 枚举Code
     */
    public static Integer getCodeByName(String name) {
        return getEnumByName(name).getCode();
    }

    /**
     * 根据枚举Code 获取枚举名称
     *
     * @param code 枚举 Code
     * @return 枚举名称
     */
    public static String getNameByCode(Integer code) {
        for (AppModelEnum type : TEMPLATE_MODEL_CACHE.values()) {
            if (type.getCode().equals(code)) {
                return type.name();
            }
        }
        // 不支持的应用类型 Code
        throw new IllegalArgumentException("The code " + code + " of " + AppModelEnum.class.getCanonicalName() + " is not supported.");
    }

    /**
     * 根据名称获取应用类型枚举
     *
     * @param name 枚举名称
     * @return 应用类型
     */
    public static AppModelEnum getEnumByName(String name) {
        if (TEMPLATE_MODEL_CACHE.containsKey(name)) {
            return TEMPLATE_MODEL_CACHE.get(name);
        }
        // 不支持的应用类型名称
        throw new IllegalArgumentException("The name " + name + " of " + AppModelEnum.class.getCanonicalName() + " is not supported.");
    }
}
