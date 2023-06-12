package com.starcloud.ops.business.app.enums.app;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用类型, 0：系统推荐应用，1：我的应用，2：下载应用
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@SuppressWarnings("unused")
public enum AppTypeEnum {

    /**
     * 系统应用：系统提供的推荐应用
     */
    SYSTEM_TEMPLATE(0, "系统应用：系统提供的推荐应用"),

    /**
     * 我的应用：我创建的应用
     */
    MY_TEMPLATE(1, "我的应用：我创建的应用"),

    /**
     * 下载应用：我已经下载的应用
     */
    DOWNLOAD_TEMPLATE(2, "下载应用：我已经下载的应用"),

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
    private final String message;

    /**
     * 用 Map 将枚举在初始化时候缓存，方便后续查询
     */
    private static final Map<String, AppTypeEnum> TEMPLATE_TYPE_CACHE = new ConcurrentHashMap<>();

    static {
        Arrays.stream(AppTypeEnum.values()).forEach(item -> TEMPLATE_TYPE_CACHE.put(item.name(), item));
    }

    /**
     * 构造函数
     *
     * @param code    应用类型 Code
     * @param message 应用类型说明
     */
    AppTypeEnum(Integer code, String message) {
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
        for (AppTypeEnum type : TEMPLATE_TYPE_CACHE.values()) {
            if (type.getCode().equals(code)) {
                return type.name();
            }
        }
        // 不支持的应用类型 Code
        throw new IllegalArgumentException("The code " + code + " of " + AppTypeEnum.class.getCanonicalName() + " is not supported.");
    }

    /**
     * 根据名称获取应用类型枚举
     *
     * @param name 枚举名称
     * @return 应用类型
     */
    public static AppTypeEnum getEnumByName(String name) {
        if (TEMPLATE_TYPE_CACHE.containsKey(name)) {
            return TEMPLATE_TYPE_CACHE.get(name);
        }
        // 不支持的应用类型名称
        throw new IllegalArgumentException("The name " + name + " of " + AppTypeEnum.class.getCanonicalName() + " is not supported.");
    }

}
