package com.starcloud.ops.business.dataset.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 数据集provider枚举
 */

@Getter
@AllArgsConstructor
public enum DatasetProviderEnum {

    SYSTEM(99,"系统")
    ;


    private final Integer status;
    private final String name;

    public static boolean isSystem(Integer status) {
        return Objects.equals(status, SYSTEM.getStatus());
    }


}
