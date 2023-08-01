package com.starcloud.ops.business.dataset.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 数据集访问权限枚举
 */

@Getter
@AllArgsConstructor
public enum DatasetPermissionEnum {

    PRIVATELY_OWNED(1,"私有"),

    TEAM_OWNED(10,"团队共享"),

    ALL(10," 所有人共享"),
            ;


    private final Integer status;
    private final String name;

    public static boolean isSystem(Integer status) {
        return Objects.equals(status, PRIVATELY_OWNED.getStatus());
    }

}
