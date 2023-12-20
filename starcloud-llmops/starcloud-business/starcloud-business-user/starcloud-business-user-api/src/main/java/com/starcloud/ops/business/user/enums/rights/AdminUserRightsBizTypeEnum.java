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

    ADMIN_MINUS(0, "管理员增加", "管理员修改权益，扣除 {} 魔法豆，{} 图片", false, false, 0, 0),
    ADMIN_ADD(1, "管理员修改", "管理员修改权益，增加 {} 魔法豆，{} 图片", true, false, 0, 0),
    REGISTER(2, "普通注册", "签到获得 {} 魔法豆，{} 图片", true, true, 10, 5),
    INVITE_TO_REGISTER(3, "邀请注册", "签到获得 {} 魔法豆，{} 图片", true, true, 10, 5),
    USER_INVITE(4, "用户推广", "签到获得 {} 魔法豆，{} 图片", true, true, 5, 2),
    USER_INVITE_REPEAT(5, "邀请达人", "签到获得 {} 魔法豆，{} 图片", true, true, 5, 2),
    SIGN(6, "签到", "签到获得 {} 魔法豆，{} 图片", true, true, 2, 1),
    REDEEM_CODE(7, "兑换码", "使用兑换码获得 {} 魔法豆，{} 图片", false, false, 0, 0),
    EXPIRE(8, " 过期", "权益过期，过期 {} 魔法豆，{} 图片", false, false, 0, 0),

    ORDER_GIVE(11, "订单权益奖励", "下单获取权益,获得 {} 魔法豆，{} 图片", true, false, 0, 0), // 支付订单时，赠送积分
    ORDER_GIVE_CANCEL(12, "订单积分奖励（整单取消）", "订单取消，退还 {} 魔法豆，{} 图片", false, false, 0, 0), // ORDER_GIVE 的取消
    ORDER_GIVE_CANCEL_ITEM(13, "订单积分奖励（单个退款）", "订单退款，扣除赠送的{} 魔法豆，{} 图片", false, false, 0, 0),// ORDER_GIVE 的取消


    //================仅限变动记录使用======Type 值上面的+50=========================================
    ADMIN_MINUS_RECORD(50, "管理员增加", "管理员修改权益，扣除 {},{} ", false, false, 0, 0),
    ADMIN_RECORD(51, "管理员修改", "管理员修改权益  增加{},{}", true, false, 0, 0),
    REGISTER_RECORD(52, "普通注册", "签到获得{},{}", true, true, 10, 5),
    INVITE_TO_REGISTER_RECORD(53, "邀请注册", "签到获得{},{}", true, true, 10, 5),
    USER_INVITE_RECORD(54, "用户推广", "签到获得{},{}", true, true, 5, 2),
    USER_INVITE_REPEAT_RECORD(55, "邀请达人", "签到获得{},{}", true, true, 5, 2),
    SIGN_RECORD(56, "签到", "签到获得{},{}", true, true, 2, 1),
    REDEEM_CODE_RECORD(57, "兑换码", "使用兑换码获得{},{}", true, false, 0, 0),
    EXPIRE_RECORD(58, " 过期", "权益过期，过期{},{}", false, false, 0, 0),

    ORDER_GIVE_RECORD(61, "订单权益奖励", "下单获取权益,获得{},{}", true, false, 0, 0), // 支付订单时，赠送积分
    ORDER_GIVE_CANCEL_RECORD(62, "订单积分奖励（整单取消）", "订单取消，退还{},{}", false, false, 0, 0), // ORDER_GIVE 的取消
    ORDER_GIVE_CANCEL_ITEM_RECORD(63, "订单积分奖励（单个退款）", "订单退款，扣除赠送的{},{}", false, false, 0, 0),// ORDER_GIVE 的取消

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
