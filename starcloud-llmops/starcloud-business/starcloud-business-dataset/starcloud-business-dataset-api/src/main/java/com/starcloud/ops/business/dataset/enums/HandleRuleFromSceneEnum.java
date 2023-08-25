package com.starcloud.ops.business.dataset.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum HandleRuleFromSceneEnum {
    SYSTEM(0,"系统规则"),
    USER(1,"用户自建"),
    ;


    private final Integer status;
    private final String name;
}
