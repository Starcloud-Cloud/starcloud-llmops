package com.starcloud.ops.business.app.enums;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Getter
public enum ValidateTypeEnum implements IEnumable<Integer> {

    /**
     * 新增校验
     */
    CREATE(1, "新增校验"),

    /**
     * 修改校验
     */
    UPDATE(2, "修改校验"),

    /**
     * 修改配置校验
     */
    CONFIG(3, "配置校验"),

    /**
     * 执行校验
     */
    EXECUTE(4, "执行校验");

    private final Integer code;

    private final String label;

    ValidateTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

}
