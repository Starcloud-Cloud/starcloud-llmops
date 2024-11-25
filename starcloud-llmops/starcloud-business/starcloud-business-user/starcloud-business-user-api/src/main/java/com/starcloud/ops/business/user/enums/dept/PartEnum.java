package com.starcloud.ops.business.user.enums.dept;

import lombok.Getter;

@Getter
public enum PartEnum {
    app("app", "应用"),
    plugin("plugin", "插件"),
    material("material", "素材库"),

    ;

    private final String code;

    private final String desc;

    PartEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
