package cn.iocoder.yudao.module.pay.enums.notify;

import cn.hutool.core.util.EnumUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 支付通知类型
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum PayNotifyTypeEnum implements IntArrayValuable {

    ORDER(1, "支付单"),
    REFUND(2, "退款单"),
    TRANSFER(3, "转账单"),
    SIGN_SUCCESS(4, "签约成功单"),
    SIGN_CLOSE(5, "签约关闭单"),
    ;

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名字
     */
    private final String name;

    /**
     * @return int 数组
     */
    @Override
    public int[] array() {
        return new int[0];
    }

    public static PayNotifyTypeEnum getByType(Integer type) {
        return EnumUtil.getBy(PayNotifyTypeEnum.class,
                e -> Objects.equals(type, e.getType()));
    }
}
