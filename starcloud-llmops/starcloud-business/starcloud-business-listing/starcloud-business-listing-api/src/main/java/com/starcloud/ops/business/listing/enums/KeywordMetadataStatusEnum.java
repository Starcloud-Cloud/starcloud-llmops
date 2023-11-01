package com.starcloud.ops.business.listing.enums;


import lombok.Getter;

@Getter
public enum KeywordMetadataStatusEnum {

    INIT(0, "数据初始化中"),

    SYNCING(1, "数据同步中"),

    ON_RETRY(2, "数据同步失败，正在重试"),

    ERROR(5, "数据获取失败"),

    NO_DATA(6, "未获取到关键词数据"),

    SUCCESS(10, "数据获取成功")

    ;


    private int code;
    private String desc;

    private KeywordMetadataStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
