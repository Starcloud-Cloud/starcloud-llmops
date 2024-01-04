package com.starcloud.ops.business.promotion.enums.common;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 兑换码类型枚举
 * <p
 * 优惠码
 * 权益码
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum PromotionCodeTypeEnum implements IntArrayValuable {

    COUPON_CODE(1, "优惠码"),
    RIGHTS_CODE(2, "权益码"),
    ;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(PromotionCodeTypeEnum::getType).toArray();

    /**
     * 类型值
     */
    private final Integer type;
    /**
     * 类型名
     */
    private final String name;

    @Override
    public int[] array() {
        return ARRAYS;
    }

}
