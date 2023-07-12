package com.starcloud.ops.business.dataset.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum DataSetStatusEnum {
    INIT(0,"初始化"),
    COMPLETED(1,"完成"),
    ERROR(2,"错误");


    private final Integer status;
    private final String name;

    public static boolean isSuccess(Integer status) {
        return Objects.equals(status, COMPLETED.getStatus());
    }
}
