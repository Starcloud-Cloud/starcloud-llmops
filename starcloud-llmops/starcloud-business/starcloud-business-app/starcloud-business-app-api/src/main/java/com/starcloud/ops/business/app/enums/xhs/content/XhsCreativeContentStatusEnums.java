package com.starcloud.ops.business.app.enums.xhs.content;

import lombok.Getter;

@Getter
public enum XhsCreativeContentStatusEnums {
    INIT("init", "初始化"),

    EXECUTING("executing", "执行中"),

    EXECUTE_SUCCESS("execute_success", "执行成功"),

    EXECUTE_ERROR("execute_error", "执行失败"),

    /**
     * 执行失败，且失败次数大于阈值时候
     */
    EXECUTE_ERROR_FINISHED("execute_error_finished", "执行最终错误");

    private String code;

    private String desc;

    XhsCreativeContentStatusEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
