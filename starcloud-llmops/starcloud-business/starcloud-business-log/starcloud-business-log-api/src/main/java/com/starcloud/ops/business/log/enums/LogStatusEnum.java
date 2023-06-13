package com.starcloud.ops.business.log.enums;

public enum LogStatusEnum {

    /**
     *
     */
    SUCCESS("成功"),

    /**
     *
     */
    ERROR("失败");


    /**
     *
     */
    private String labs;

    LogStatusEnum(String title) {
        this.labs = title;
    }
}
