package com.starcloud.ops.business.app.enums.operate;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-12
 */
public enum AppOperateTypeEnum implements IEnumable<Integer> {
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
    private final String label;

    /**
     * 构造函数
     *
     * @param code  应用操作类型 Code
     * @param label 应用操作类型说明
     */
    AppOperateTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
