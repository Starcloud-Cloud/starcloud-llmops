package com.starcloud.ops.business.user.enums.rights;

import cn.hutool.core.util.EnumUtil;
import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 会员权益的业务类型枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum AdminUserRightsBizTypeEnum implements IntArrayValuable {

    REGISTER(1, "普通注册", "签到获得 {} 魔法豆，{} 图片", true, true, 10, 5),
    INVITE_TO_REGISTER(2, "邀请注册", "签到获得 {} 魔法豆，{} 图片", true, true, 10, 5),
    USER_INVITE(3, "用户推广", "签到获得 {} 魔法豆，{} 图片", true, true, 5, 2),
    USER_INVITE_REPEAT(4, "邀请达人", "签到获得 {} 魔法豆，{} 图片", true, true, 5, 2),
    SIGN(5, "签到", "签到获得 {} 魔法豆，{} 图片", true, true, 2, 1),

    ADMIN(6, "管理员修改", "管理员修改 {} 魔法豆，{} 图片", true, false, 0, 0),

    REDEEM_CODE(11, "兑换码", "使用兑换码获得 {} 魔法豆，{} 图片", true, false, 0, 0),

    EXPIRE(16, " 过期", "权益过期，过期 {} 魔法豆，{} 图片", false, false, 0, 0),

    ORDER_GIVE(21, "订单权益奖励", "下单获取权益,获得 {} 魔法豆，{} 图片", true, false, 0, 0), // 支付订单时，赠送积分
    ORDER_GIVE_CANCEL(22, "订单积分奖励（整单取消）", "订单取消，退还 {} 魔法豆，{} 图片", false, false, 0, 0), // ORDER_GIVE 的取消
    ORDER_GIVE_CANCEL_ITEM(23, "订单积分奖励（单个退款）", "订单退款，扣除赠送的{} 魔法豆，{} 图片", false, false, 0, 0),// ORDER_GIVE 的取消


    //================仅限变动记录使用===============================================
    REGISTER_RECORD(3501, "普通注册", "签到获得 {} 魔法豆，{} 图片", true,  false, 0, 0),
    INVITE_TO_REGISTER_RECORD(3502, "邀请注册", "签到获得 {} 魔法豆，{} 图片", true, false, 0, 0),
    USER_INVITE_RECORD(3503, "用户推广", "签到获得 {} 魔法豆，{} 图片", true,  false, 0, 0),
    USER_INVITE_REPEAT_RECORD(3504, "邀请达人", "签到获得 {} 魔法豆，{} 图片", true, false, 0, 0),
    SIGN_RECORD(3505, "签到", "签到获得 {} ，获取的数量为{} ", true, false, 0, 0),
    ADMIN_RECORD(3506, "管理员修改", "管理员修改 {} ，获取的数量为{} ", true, false, 0, 0),

    REDEEM_CODE_RECORD(3511, "兑换码", "使用兑换码获得 {} ，获取的数量为{} ", true, false, 0, 0),

    EXPIRE_RECORD(3516, " 过期", "权益过期，过期 {} ，过期的数量为{} ", false, false, 0, 0),

    ORDER_GIVE_RECORD(3521, "订单权益奖励", "下单获取权益,获得 {} ，获取的数量为{} ", true, false, 0, 0), // 支付订单时，赠送积分
    ORDER_GIVE_CANCEL_RECORD(3522, "订单积分奖励（整单取消）", "订单取消，退还 {} ，退还的数量为{} ", false, false, 0, 0), // ORDER_GIVE 的取消
    ORDER_GIVE_CANCEL_ITEM_RECORD(3523, "订单积分奖励（单个退款）", "订单退款，扣除赠送的{} ，扣除的数量为{} ", false, false, 0, 0) // ORDER_GIVE 的取消

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
    /**
     * 是否为扣减积分
     */
    private final boolean add;
    /**
     * 是否为系统配置
     */
    private final boolean system;
    /**
     * 魔法豆
     */
    private final Integer magicBean;
    /**
     * 图片
     */
    private final Integer magicImage;

    @Override
    public int[] array() {
        return new int[0];
    }

    public static AdminUserRightsBizTypeEnum getByType(Integer type) {
        return EnumUtil.getBy(AdminUserRightsBizTypeEnum.class,
                e -> Objects.equals(type, e.getType()));
    }

}
