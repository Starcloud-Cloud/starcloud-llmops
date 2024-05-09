package com.starcloud.ops.business.app.enums.xhs.material;

import lombok.Getter;

@Getter
public enum ImitateTypeEnum {
    content("内容"),
    format("格式"),
    style("文风"),
    ;


    private String code;

    private String desc;

    ImitateTypeEnum(String desc) {
        this.desc = desc;
    }

    public static ImitateTypeEnum of(String code) {
        return null;
    }
}
