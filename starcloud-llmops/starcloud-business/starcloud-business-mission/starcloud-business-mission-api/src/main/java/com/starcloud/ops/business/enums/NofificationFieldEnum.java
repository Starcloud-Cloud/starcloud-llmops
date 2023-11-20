package com.starcloud.ops.business.enums;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

@Getter
public enum NofificationFieldEnum implements IEnumable<String> {
    ;

    private String code;

    private String desc;

    NofificationFieldEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getLabel() {
        return code;
    }
}
