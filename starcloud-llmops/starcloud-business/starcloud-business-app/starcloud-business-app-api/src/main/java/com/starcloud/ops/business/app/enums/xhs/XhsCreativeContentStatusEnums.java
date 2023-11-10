package com.starcloud.ops.business.app.enums.xhs;

import lombok.Getter;

@Getter
public enum XhsCreativeContentStatusEnums {
    INIT("init", "初始化"),

    EXECUTING("executing", "执行中"),

    EXECUTE_SUCCESS("execute_success", "执行成功"),

    EXECUTE_ERROR("execute_error", "执行失败")

    ;

    private String code;

    private String desc;

    XhsCreativeContentStatusEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
