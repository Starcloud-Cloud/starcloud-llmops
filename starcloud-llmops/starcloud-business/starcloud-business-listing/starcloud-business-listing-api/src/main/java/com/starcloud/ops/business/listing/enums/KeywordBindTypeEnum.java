package com.starcloud.ops.business.listing.enums;

import lombok.Getter;

@Getter
public enum KeywordBindTypeEnum {

    dict("词库"),

    draft("草稿");

    private String desc;

    KeywordBindTypeEnum(String desc) {
        this.desc = desc;
    }
}
