package com.starcloud.ops.business.log.enums;

public enum LogMessageTypeEnum {


    CHAT(1,"普通聊天"),

    CHAT_FUN(11,"LLM函数触发"),

    FUN_CALL(12,"函数执行"),

    FUN_DONE(13,"LLM函数执行完毕"),

    FUN_SUMMARY(14,"LLM函数执行总结"),

    SUMMARY(2,"总结")

    ;


    /**
     * message类型Code
     */
    private Integer code;

    /**
     * 类型说明
     */
    private String label;

    LogMessageTypeEnum(Integer code, String label) {
        this.code = code;
        this.label = label;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
