package com.starcloud.ops.business.app.enums.materiallibrary;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 素材类型枚举
 */
@Getter
@AllArgsConstructor
public enum ColumnTypeEnum implements IntArrayValuable {

    STRING(0, "字符串"),
    INTEGER(1, "数字"),
    TIME(1, "时间"),
    NUMBER(1, "数字"),
    BOOLEAN(1, "布尔值"),
    IMAGE(1, "图片"),
    ;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(ColumnTypeEnum::getStatus).toArray();

    /**
     * 状态值
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public int[] array() {
        return ARRAYS;
    }

}
