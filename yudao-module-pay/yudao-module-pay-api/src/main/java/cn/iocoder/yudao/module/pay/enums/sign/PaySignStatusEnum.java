package cn.iocoder.yudao.module.pay.enums.sign;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 支付订单的状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum PaySignStatusEnum implements IntArrayValuable {

    WAITING(0, "未签约"),
    SUCCESS(10, "签约成功"),
    REFUND(20, "签约失败或退款"),
    CLOSED(30, "取消签约"), //
    ;

    private final Integer status;
    private final String name;

    @Override
    public int[] array() {
        return new int[0];
    }

    /**
     * 判断是否支付成功
     *
     * @param status 状态
     * @return 是否支付成功
     */
    public static boolean isWaiting(Integer status) {
        return Objects.equals(status, WAITING.getStatus());
    }

    /**
     * 判断是否支付成功
     *
     * @param status 状态
     * @return 是否支付成功
     */
    public static boolean isSuccess(Integer status) {
        return Objects.equals(status, SUCCESS.getStatus());
    }

    /**
     * 判断是否支付成功或者已退款
     *
     * @param status 状态
     * @return 是否支付成功或者已退款
     */
    public static boolean isSuccessOrRefund(Integer status) {
        return ObjectUtils.equalsAny(status,
                SUCCESS.getStatus(), REFUND.getStatus());
    }

    /**
     * 判断是否支付关闭
     *
     * @param status 状态
     * @return 是否支付关闭
     */
    public static boolean isClosed(Integer status) {
        return Objects.equals(status, CLOSED.getStatus());
    }

}
