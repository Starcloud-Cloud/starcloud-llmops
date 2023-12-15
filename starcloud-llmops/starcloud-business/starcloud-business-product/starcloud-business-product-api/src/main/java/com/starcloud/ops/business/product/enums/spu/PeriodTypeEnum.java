package com.starcloud.ops.business.product.enums.spu;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 商品 SPU 状态
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum PeriodTypeEnum implements IntArrayValuable {

    DAY(1, " 日"),
    WEEK(7, "周"),
    MONTH(31, "月"),
    YEAR(365, "年");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(PeriodTypeEnum::getStatus).toArray();

    /**
     * 状态
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
