package com.starcloud.ops.business.poster.enums.material;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * 素材类似枚举
 */
@Getter
@AllArgsConstructor
public enum MaterialTypeEnum implements IntArrayValuable {


    IMAGE(1, "图片"),
    FONT(2, "字体"),
    TEMPLATE(3, "模板"),
    ELEMENT(3, "元素"),
    ;


    /**
     * 状态
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    ;

    /**
     * @return int 数组
     */
    @Override
    public int[] array() {
        return new int[0];
    }
}
