package com.starcloud.ops.business.listing.enums;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

@Getter
public enum DraftTypeEnum implements IEnumable<Integer> {

    ADD_LISTING(1, "新增listing"),

    OPTIMIZATION_LISTING(2, "优化listing")
    ;

    private final Integer code;

    private final String label;


    DraftTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
