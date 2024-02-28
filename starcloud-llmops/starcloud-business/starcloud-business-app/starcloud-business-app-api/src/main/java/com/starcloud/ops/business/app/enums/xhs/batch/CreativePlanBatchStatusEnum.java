package com.starcloud.ops.business.app.enums.xhs.batch;

import lombok.Getter;

@Getter
public enum CreativePlanBatchStatusEnum {

    RUNNING(10, "执行中"),

    SUCCESS(20, "执行成功"),

    FAILURE(30, "执行失败")
    ;


    private final Integer code;

    private final String label;

    CreativePlanBatchStatusEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }
}
