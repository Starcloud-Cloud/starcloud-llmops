package com.starcloud.ops.business.app.enums.materiallibrary;

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

    STRING(0, "文本"),
    IMAGE(5, "图片"),
    DOCUMENT(6,"文档路径"),

    ;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(ColumnTypeEnum::getCode).toArray();

    /**
     * 状态值
     */
    private final Integer code;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public int[] array() {
        return ARRAYS;
    }

}
