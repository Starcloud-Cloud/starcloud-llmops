package com.starcloud.ops.business.promotion.enums.promocode;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 优惠劵状态枚举
 *
 * @author Cusack Alan
 */
@AllArgsConstructor
@Getter
public enum PromoCodeStatusEnum implements IntArrayValuable {

    ENABLE(1, "启用"),
    CLOSE(2, "关闭"),
    EXPIRE(3, "过期"),
    ;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(PromoCodeStatusEnum::getStatus).toArray();

    /**
     * 值
     */
    private final Integer status;
    /**
     * 名字
     */
    private final String name;

    @Override
    public int[] array() {
        return ARRAYS;
    }

}
