package com.starcloud.ops.business.app.enums.operate;

import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
public enum AppOperateTypeEnum {
    /**
     * 点赞
     */
    LIKE(0, "点赞"),

    /**
     * 查看
     */
    VIEW(1, "查看"),

    /**
     * 下载
     */
    DOWNLOAD(3, "下载"),

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
     * 构造函数
     *
     * @param code    应用操作类型 Code
     * @param message 应用操作类型说明
     */
    AppOperateTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static AppOperateTypeEnum getByName(String name) {
        for (AppOperateTypeEnum value : AppOperateTypeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }

}
