package com.starcloud.ops.business.listing.enums;


import lombok.Getter;

@Getter
public enum KeywordTypeEnum {

    singleWord(0, "单词"),

    phrases(1, "词组"),

    useless(2, "未挖掘到数据的词")

    ;


    private int code;
    private String desc;

    private KeywordTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
