package com.starcloud.ops.business.app.enums.xhs.material;

import lombok.Getter;

@Getter
public enum ImitateTypeEnum {
    content("content", "内容"),
    format("format", "格式"),
    style("Style", "文风"),
    ;


    private String code;

    private String desc;

    ImitateTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ImitateTypeEnum of(String code) {
        return null;
    }
}
