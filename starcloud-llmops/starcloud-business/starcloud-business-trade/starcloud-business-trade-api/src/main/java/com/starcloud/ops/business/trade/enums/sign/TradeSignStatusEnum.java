package com.starcloud.ops.business.trade.enums.sign;

import cn.hutool.core.util.ObjectUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 签约订单 - 状态
 *
 * @author Sin
 */
@RequiredArgsConstructor
@Getter
public enum TradeSignStatusEnum implements IntArrayValuable {

    UN_SIGN(0, "待签约"),
    SIGNING(30, "签约中"),
    CANCELED(40, "取消签约");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(TradeSignStatusEnum::getStatus).toArray();

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

    // ========== 问：为什么写了很多 isXXX 和 haveXXX 的判断逻辑呢？ ==========
    // ========== 答：方便找到某一类判断，哪些业务正在使用 ==========

    /**
     * 判断指定状态，是否正处于【待签约】状态
     *
     * @param status 指定状态
     * @return 是否
     */
    public static boolean isUnpaid(Integer status) {
        return ObjectUtil.equal(UN_SIGN.getStatus(), status);
    }

    /**
     * 判断指定状态，是否正处于【取消签约】状态
     *
     * @param status 指定状态
     * @return 是否
     */
    public static boolean isCanceled(Integer status) {
        return ObjectUtil.equals(status, CANCELED.getStatus());
    }

    /**
     * 判断指定状态，是否正处于【签约中】状态
     *
     * @param status 指定状态
     * @return 是否
     */
    public static boolean isCompleted(Integer status) {
        return ObjectUtil.equals(status, SIGNING.getStatus());
    }


}
