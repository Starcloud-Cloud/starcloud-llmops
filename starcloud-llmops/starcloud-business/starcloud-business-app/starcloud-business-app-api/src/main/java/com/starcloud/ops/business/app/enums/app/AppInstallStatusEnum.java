package com.starcloud.ops.business.app.enums.app;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * 应用安装状态信息
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-18
 */
@SuppressWarnings("unused")
public enum AppInstallStatusEnum implements IEnumable<Integer> {

    /**
     * 未安装
     */
    UNINSTALLED(0, "未安装"),

    /**
     * 已安装
     */
    INSTALLED(1, "已安装"),

    /**
     * 需要更新
     */
    UPDATE(2, "需要更新"),

    ;

    /**
     * 应用安装状态 Code
     */
    @Getter
    private final Integer code;

    /**
     * 应用安装状态说明
     */
    @Getter
    private final String label;

    /**
     * 构造函数
     *
     * @param code  应用安装状态 Code
     * @param label 应用安装状态说明
     */
    AppInstallStatusEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
