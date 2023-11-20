package com.starcloud.ops.business.enums;

import com.starcloud.ops.framework.common.api.enums.IEnumable;
import lombok.Getter;

@Getter
public enum MisssionTypeEnum implements IEnumable<String> {

    posting("posting", "发帖任务");

    private String code;

    private String desc;

    MisssionTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getLabel() {
        return code;
    }

}
