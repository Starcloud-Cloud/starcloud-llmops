package com.starcloud.ops.business.listing.enums;

import lombok.Getter;

@Getter
public enum ListExecuteEnum {


    EXECUTING(4, "执行中"),

    EXECUTE_ERROR(5, "执行失败"),

    EXECUTED(6, "执行结束");

    private int code;

    private String desc;

    ListExecuteEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
