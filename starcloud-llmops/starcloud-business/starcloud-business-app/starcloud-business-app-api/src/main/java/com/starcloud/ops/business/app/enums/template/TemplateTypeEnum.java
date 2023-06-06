package com.starcloud.ops.business.app.enums.template;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模版类型, 0：系统推荐模版，1：我的模版，2：下载模版
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@SuppressWarnings("unused")
public enum TemplateTypeEnum {

    /**
     * 系统模版：系统提供的推荐模版
     */
    SYSTEM_TEMPLATE(0, "系统模版：系统提供的推荐模版"),

    /**
     * 我的模版：我创建的模版
     */
    MY_TEMPLATE(1, "我的模版：我创建的模版"),

    /**
     * 下载模版：我已经下载的模版
     */
    DOWNLOAD_TEMPLATE(2, "下载模版：我已经下载的模版"),

    ;

    /**
     * 模版类型Code
     */
    @Getter
    private final Integer code;

    /**
     * 模版类型说明
     */
    @Getter
    private final String message;

    /**
     * 用 Map 将枚举在初始化时候缓存，方便后续查询
     */
    private static final Map<String, TemplateTypeEnum> TEMPLATE_TYPE_CACHE = new ConcurrentHashMap<>();

    static {
        Arrays.stream(TemplateTypeEnum.values()).forEach(item -> TEMPLATE_TYPE_CACHE.put(item.name(), item));
    }

    /**
     * 构造函数
     *
     * @param code    模版类型 Code
     * @param message 模版类型说明
     */
    TemplateTypeEnum(Integer code, String message) {
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
        for (TemplateTypeEnum type : TEMPLATE_TYPE_CACHE.values()) {
            if (type.getCode().equals(code)) {
                return type.name();
            }
        }
        // 不支持的模版类型 Code
        throw new IllegalArgumentException("The code " + code + " of " + TemplateTypeEnum.class.getCanonicalName() + " is not supported.");
    }

    /**
     * 根据名称获取模版类型枚举
     *
     * @param name 枚举名称
     * @return 模版类型
     */
    public static TemplateTypeEnum getEnumByName(String name) {
        if (TEMPLATE_TYPE_CACHE.containsKey(name)) {
            return TEMPLATE_TYPE_CACHE.get(name);
        }
        // 不支持的模版类型名称
        throw new IllegalArgumentException("The name " + name + " of " + TemplateTypeEnum.class.getCanonicalName() + " is not supported.");
    }

}
