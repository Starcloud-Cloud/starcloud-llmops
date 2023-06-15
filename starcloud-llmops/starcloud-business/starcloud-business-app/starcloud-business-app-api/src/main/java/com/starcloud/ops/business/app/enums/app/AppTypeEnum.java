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
    SYSTEM(0, "系统应用：系统提供的推荐应用"),

    /**
     * 我的应用：我创建的应用
     */
    MYSELF(1, "我的应用：我创建的应用"),

    /**
     * 下载应用：我已经下载的应用
     */
    DOWNLOAD(2, "下载应用：我已经下载的应用"),

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
     * 枚举缓存
     *
     * @return 枚举缓存
     */
    public static Map<String, AppTypeEnum> cache() {
        return TEMPLATE_TYPE_CACHE;
    }

}
