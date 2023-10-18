package com.starcloud.ops.business.listing.enums;

public enum AnalysisStatusEnum {

    ANALYSIS(0,"分析中"),

    ANALYSIS_ERROR(1,"分析失败"),

    ANALYSIS_END(2, "分析结束"),

    EXECUTING(3, "执行中"),

    EXECUTE_ERROR(4, "执行失败"),

    EXECUTED(5, "执行结束"),

    ;

    private int code;

    private String desc;

    AnalysisStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
