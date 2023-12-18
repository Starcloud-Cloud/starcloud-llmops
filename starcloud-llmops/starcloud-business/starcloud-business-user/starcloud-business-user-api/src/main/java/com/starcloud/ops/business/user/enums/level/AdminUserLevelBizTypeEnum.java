package com.starcloud.ops.business.user.enums.level;

import cn.hutool.core.util.EnumUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 系统会员等级的业务类型枚举
 *
 * @author Alan Cusack
 */
@AllArgsConstructor
@Getter
public enum AdminUserLevelBizTypeEnum implements IntArrayValuable {

    ADMIN(99, "管理员修改", "管理员修改 {} 用户等级"),

    ORDER_GIVE(21, "订单用户等级奖励", "下单获得 {} 用户等级"), // 支付订单时，赠送积分
    ORDER_GIVE_CANCEL(22, "订单用户等级奖励（整单取消）", "订单取消，退还 {} 用户等级"), // ORDER_GIVE 的取消
    ORDER_GIVE_CANCEL_ITEM(23, "订单用户等级奖励（单个退款）", "订单退款，扣除赠送的 {} 用户等级") // ORDER_GIVE 的取消
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
     * 描述
     */
    private final String description;


    @Override
    public int[] array() {
        return new int[0];
    }

    public static AdminUserLevelBizTypeEnum getByType(Integer type) {
        return EnumUtil.getBy(AdminUserLevelBizTypeEnum.class,
                e -> Objects.equals(type, e.getType()));
    }

}