package com.starcloud.ops.business.listing.enums;

public enum AnalysisStatusEnum {

    ANALYSIS(1,"分析中"),

    ANALYSIS_ERROR(2,"分析失败"),

    ANALYSIS_END(3, "分析结束"),

    EXECUTING(4, "执行中"),

    EXECUTE_ERROR(5, "执行失败"),

    EXECUTED(6, "执行结束"),

    ;

    private int code;

    private String desc;

    AnalysisStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
