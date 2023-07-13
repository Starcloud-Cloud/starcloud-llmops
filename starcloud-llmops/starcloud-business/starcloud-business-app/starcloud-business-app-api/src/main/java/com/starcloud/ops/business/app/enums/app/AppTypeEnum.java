package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 应用类型, 0：系统推荐应用，1：我的应用，2：下载应用
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@SuppressWarnings("unused")
public enum AppTypeEnum implements IEnumable<Integer> {

    /**
     * 我的应用：我创建的应用
     */
    MYSELF(0, "我的应用：我创建的应用"),

    /**
     * 下载安装的应用：我已经下载安装的应用
     */
    INSTALLED(1, "下载应用：我已经下载的应用"),

    /**
     * 已经发布的应用：已经发布的应用
     */
    PUBLISHED(2, "已经发布的应用：已经发布的应用"),

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
    private final String label;

    /**
     * 构造函数
     *
     * @param code  应用类型 Code
     * @param label 应用类型说明
     */
    AppTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
