package cn.iocoder.yudao.module.pay.enums.sign;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 支付签约的状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum PaySignStatusEnum implements IntArrayValuable {

    WAITING(0, "待签约"),
    SUCCESS(10, "签约成功"),
    CANCEL(20, "取消签约"),
    CLOSED(30, "签约关闭"),
    ;

    private final Integer status;
    private final String name;

    @Override
    public int[] array() {
        return new int[0];
    }

    /**
     * 判断是否签约成功
     *
     * @param status 状态
     * @return 是否签约成功
     */
    public static boolean isSuccess(Integer status) {
        return Objects.equals(status, SUCCESS.getStatus());
    }

    /**
     * 判断是否取消订阅
     *
     * @param status 状态
     * @return 是否取消订阅
     */
    public static boolean isCancelSign(Integer status) {
        return Objects.equals(status, CANCEL.getStatus());
    }

    /**
     * 判断是否签约关闭 -未签约 超时关闭
     *
     * @param status 状态
     * @return 是否签约关闭
     */
    public static boolean isClosed(Integer status) {
        return Objects.equals(status, CLOSED.getStatus());
    }

}
