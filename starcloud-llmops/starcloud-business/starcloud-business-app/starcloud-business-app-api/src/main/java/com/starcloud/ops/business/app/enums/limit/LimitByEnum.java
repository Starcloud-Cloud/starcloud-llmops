package com.starcloud.ops.business.app.enums.limit;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-30
 */
@Getter
public enum LimitByEnum implements IEnumable<Integer> {


    ;

    /**
     *
     */
    private final Integer code;

    private final String label;

    LimitByEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
