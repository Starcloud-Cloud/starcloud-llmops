package com.starcloud.ops.business.log.enums;

public enum LogTimeTypeEnum {

    /**
     *
     */
    TODAY("今天"),

    /**
     *
     */
    LAST_4W("过去4周"),

    LAST_3M("过去3月"),

    LAST_12M("过去12月"),

    LAST_M_T("本月至今"),

    LAST_Q_T("本季度至今"),

    LAST_Y_T("本年至今"),

    ALL("所有时间");


    /**
     *
     */
    private String labs;

    LogTimeTypeEnum(String title) {
        this.labs = title;
    }
}
