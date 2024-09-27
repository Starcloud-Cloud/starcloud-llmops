package com.starcloud.ops.business.app.enums.plugin;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum PluginBindTypeEnum {
    owner(1, "用户自己的绑定"),
    sys(2, "系统生成的绑定-用户不可删除");

    private final int code;

    private final String desc;

    PluginBindTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static boolean isSys(Integer typeCode) {
        return Objects.equals(typeCode, sys.getCode());
    }
}
