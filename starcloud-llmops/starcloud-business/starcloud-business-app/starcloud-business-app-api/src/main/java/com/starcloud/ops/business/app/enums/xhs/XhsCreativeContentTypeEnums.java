package com.starcloud.ops.business.app.enums.xhs;

import lombok.Getter;

@Getter
public enum XhsCreativeContentTypeEnums {
    PICTURE("picture","图片"),

    COPY_WRITING("copy_writing","文字模板")

    ;


    private String code;

    private String desc;

    XhsCreativeContentTypeEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
