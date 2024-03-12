package com.starcloud.ops.business.app.enums.xhs;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Getter
public enum CreativeOptionModelEnum implements IEnumable<Integer> {

    BASE_INFO(1, "基本信息", "BASE"),

    STEP_RESPONSE(2, "步骤响应", "STEP"),

    MATERIAL(3, "素材", "MATERIAL"),
    ;

    private final Integer code;

    private final String label;

    private final String prefix;

    CreativeOptionModelEnum(Integer code, String label, String prefix) {
        this.code = code;
        this.label = label;
        this.prefix = prefix;
    }

    public static CreativeOptionModelEnum of(String name) {
        for (CreativeOptionModelEnum value : values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
