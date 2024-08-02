package com.starcloud.ops.business.app.enums.xhs.material;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Getter
public enum MaterialUsageModel implements IEnumable<Integer> {

    /**
     * 过滤未使用素材
     */
    FILTER_USAGE(1, "过滤已使用素材"),

    /**
     * 选择素材
     */
    SELECT(2, "选择素材");

    private final Integer code;

    private final String label;

    MaterialUsageModel(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * 通过model获取枚举
     *
     * @param model model
     * @return 枚举
     */
    public static MaterialUsageModel fromName(String model) {

        for (MaterialUsageModel value : MaterialUsageModel.values()) {
            if (value.name().equalsIgnoreCase(model)) {
                return value;
            }
        }
        return FILTER_USAGE;
    }
}
