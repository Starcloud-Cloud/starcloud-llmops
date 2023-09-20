package com.starcloud.ops.business.dataset.enums;

import lombok.Getter;

@Getter
public enum EmbeddingTypeEnum {

    DOCUMENT("文档"),

    QUERY("问答")
    ;

    private final String desc;


    EmbeddingTypeEnum(String desc) {
        this.desc = desc;
    }
}
