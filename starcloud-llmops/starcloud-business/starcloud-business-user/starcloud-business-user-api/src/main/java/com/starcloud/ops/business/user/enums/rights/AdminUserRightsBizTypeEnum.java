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

    ADMIN_MINUS(0, "管理员修改", "管理员修改权益，扣除 {} 魔法豆，{} 图片", false, false, 0, 0,0),
    ADMIN_ADD(1, "管理员增加", "管理员修改权益，增加 {} 魔法豆，{} 图片", true, false, 0, 0, 0),
    REGISTER(2, "普通注册", "普通注册获得 {} 魔法豆，{} 图片", true, true, 10, 5,0),
    INVITE_TO_REGISTER(3, "邀请注册", "邀请注册获得 {} 魔法豆，{} 图片", true, true, 10, 5, 0),
    USER_INVITE(4, "用户推广", "用户推广获得 {} 魔法豆，{} 图片", true, true, 5, 2,20),
    USER_INVITE_REPEAT(5, "邀请达人", "邀请达标获得 {} 魔法豆，{} 图片", true, true, 5, 2,0),
    SIGN(6, "签到", "签到获得 {} 魔法豆，{} 图片", true, true, 2, 1,0),
    REDEEM_CODE(7, "兑换码", "使用兑换码获得 {} 魔法豆，{} 图片", false, false, 0, 0,0),
    EXPIRE(8, " 过期", "权益过期，过期 {} 魔法豆，{} 图片", false, false, 0, 0,0),

    ORDER_GIVE(11, "订单权益奖励", "下单获取权益,获得 {} 魔法豆，{} 图片", true, false, 0, 0,0), // 支付订单时，赠送积分
    ORDER_GIVE_CANCEL(12, "订单积分奖励（整单取消）", "订单取消，退还 {} 魔法豆，{} 图片", false, false, 0, 0,0), // ORDER_GIVE 的取消
    ORDER_GIVE_CANCEL_ITEM(13, "订单积分奖励（单个退款）", "订单退款，扣除赠送的{} 魔法豆，{} 图片", false, false, 0, 0,0),// ORDER_GIVE 的取消

    // 场景
    WEB_ADMIN_SCENE(14, "创作中心", "创作中心，扣除 {}，{} ", false, false, 0, 0,0),
    CHAT_TEST_SCENE(15, "聊天调试", "聊天调试，扣除 {}，{} ", false, false, 0, 0,0),
    WEB_MARKET_SCENE(16, "应用市场", "应用市场，扣除 {}，{} ", false, false, 0, 0,0),
    CHAT_MARKET_SCENE(17, "员工广场", "员工广场，扣除 {}，{} ", false, false, 0, 0,0),
    WEB_IMAGE_SCENE(18, "AI自由绘图", "AI自由绘图，扣除 {}，{} ", false, false, 0, 0,0),
    IMAGE_UPSCALING_SCENE(19, "图片高清放大", "图片高清放大，扣除 {}，{} ", false, false, 0, 0,0),
    IMAGE_REMOVE_BACKGROUND_SCENE(20, "图片去背景", "图片去背景，扣除 {}，{} ", false, false, 0, 0,0),
    IMAGE_REPLACE_BACKGROUND_SCENE(21, "图片替换背景", "图片替换背景，扣除 {}，{} ", false, false, 0,0, 0),
    IMAGE_REMOVE_TEXT_SCENE(22, "图片去文字", "图片去文字，扣除 {}，{} ", false, false, 0, 0,0),
    IMAGE_SKETCH_SCENE(23, "草图生成图片", "草图生成图片，扣除 {}，{} ", false, false, 0, 0,0),
    IMAGE_VARIANTS_SCENE(24, "图片裂变", "图片裂变，扣除 {}，{} ", false, false, 0, 0,0),
    OPTIMIZE_PROMPT_SCENE(25, "优化提示词", "优化提示词，扣除 {}，{} ", false, false, 0, 0,0),
    LISTING_GENERATE_SCENE(26, "Listing生成", "Listing生成，扣除 {}，{} ", false, false, 0, 0,0),
    XHS_WRITING_SCENE(27, "小红书文案", "小红书文案，扣除 {}，{} ", false, false, 0, 0,0),
    SHARE_WEB_SCENE(28, "页面分享", "页面分享，扣除 {}，{} ", false, false, 0, 0,0),
    SHARE_IFRAME_SCENE(29, "iframe分享", "iframe分享，扣除 {}，{} ", false, false, 0, 0,0),
    SHARE_JS_SCENE(30, "JS分享", "JS分享，扣除 {}，{} ", false, false, 0, 0,0),
    SHARE_API_SCENE(31, "API调用", "API调用，扣除 {}，{} ", false, false, 0, 0,0),
    WECOM_GROUP_SCENE(32, "企业微信群", "企业微信群，扣除 {}，{} ", false, false, 0, 0,0),
    MP_SCENE(33, "微信公共号", "微信公共号，扣除 {}，{} ", false, false, 0, 0,0),

    //================仅限变动记录使用======Type 值上面的+50=========================================
    ADMIN_MINUS_RECORD(50, "管理员增加", "管理员修改权益，扣除 {},{} ", false, false, 0, 0,0),
    ADMIN_RECORD(51, "管理员修改", "管理员修改权益  增加{},{}", true, false, 0, 0,0),
    REGISTER_RECORD(52, "普通注册", "普通注册获得{},{}", true, true, 10, 5,0),
    INVITE_TO_REGISTER_RECORD(53, "普通注册", "普通注册获得{},{}", true, true, 10, 5,0),
    USER_INVITE_RECORD(54, "用户推广", "用户推广获得{},{}", true, true, 5, 2,0),
    USER_INVITE_REPEAT_RECORD(55, "邀请达人", "邀请达标获得{},{}", true, true, 5, 2,0),
    SIGN_RECORD(56, "签到", "签到获得{},{}", true, true, 2, 1,0),
    REDEEM_CODE_RECORD(57, "兑换码", "使用兑换码获得{},{}", true, false, 0, 0,0),
    EXPIRE_RECORD(58, " 过期", "权益过期，过期{},{}", false, false, 0, 0,0),

    ORDER_GIVE_RECORD(61, "订单权益奖励", "下单获取权益,获得{},{}", true, false, 0, 0,0), // 支付订单时，赠送积分
    ORDER_GIVE_CANCEL_RECORD(62, "订单积分奖励（整单取消）", "订单取消，退还{},{}", false, false, 0, 0,0), // ORDER_GIVE 的取消
    ORDER_GIVE_CANCEL_ITEM_RECORD(63, "订单积分奖励（单个退款）", "订单退款，扣除赠送的{},{}", false, false, 0, 0,0),// ORDER_GIVE 的取消

    // 场景
    WEB_ADMIN_SCENE_RECORD(64, "创作中心", "创作中心，扣除 {}，{} ", false, false, 0, 0,0),
    CHAT_TEST_SCENE_RECORD(65, "聊天调试", "聊天调试，扣除 {}，{} ", false, false, 0, 0,0),
    WEB_MARKET_SCENE_RECORD(66, "应用市场", "应用市场，扣除 {}，{} ", false, false, 0, 0,0),
    CHAT_MARKET_SCENE_RECORD(67, "员工广场", "员工广场，扣除 {}，{} ", false, false, 0, 0,0),
    WEB_IMAGE_SCENE_RECORD(68, "AI自由绘图", "AI自由绘图，扣除 {}，{} ", false, false, 0, 0,0),
    IMAGE_UPSCALING_SCENE_RECORD(69, "图片高清放大", "图片高清放大，扣除 {}，{} ", false, false, 0, 0,0),
    IMAGE_REMOVE_BACKGROUND_SCENE_RECORD(70, "图片去背景", "图片去背景，扣除 {}，{} ", false, false, 0, 0,0),
    IMAGE_REPLACE_BACKGROUND_SCENE_RECORD(71, "图片替换背景", "图片替换背景，扣除 {}，{} ", false, false, 0, 0,0),
    IMAGE_REMOVE_TEXT_SCENE_RECORD(72, "图片去文字", "图片去文字，扣除 {}，{} ", false, false, 0, 0,0),
    IMAGE_SKETCH_SCENE_RECORD(73, "草图生成图片", "草图生成图片，扣除 {}，{} ", false, false, 0, 0,0),
    IMAGE_VARIANTS_SCENE_RECORD(74, "图片裂变", "图片裂变，扣除 {}，{} ", false, false, 0, 0,0),
    OPTIMIZE_PROMPT_SCENE_RECORD(75, "优化提示词", "优化提示词，扣除 {}，{} ", false, false, 0, 0,0),
    LISTING_GENERATE_SCENE_RECORD(76, "Listing生成", "Listing生成，扣除 {}，{} ", false, false, 0, 0,0),
    XHS_WRITING_SCENE_RECORD(77, "小红书文案", "小红书文案，扣除 {}，{} ", false, false, 0, 0,0),
    SHARE_WEB_SCENE_RECORD(78, "页面分享", "页面分享，扣除 {}，{} ", false, false, 0, 0,0),
    SHARE_IFRAME_SCENE_RECORD(79, "iframe分享", "iframe分享，扣除 {}，{} ", false, false, 0, 0,0),
    SHARE_JS_SCENE_RECORD(80, "JS分享", "JS分享，扣除 {}，{} ", false, false, 0, 0,0),
    SHARE_API_SCENE_RECORD(81, "API调用", "API调用，扣除 {}，{} ", false, false, 0, 0,0),
    WECOM_GROUP_SCENE_RECORD(82, "企业微信群", "企业微信群，扣除 {}，{} ", false, false, 0, 0,0),
    MP_SCENE_RECORD(83, "微信公共号", "微信公共号，扣除 {}，{} ", false, false, 0, 0,0),
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
    private final Integer matrixBean;

    @Override
    public int[] array() {
        return new int[0];
    }

    public static AdminUserRightsBizTypeEnum getByType(Integer type) {
        return EnumUtil.getBy(AdminUserRightsBizTypeEnum.class,
                e -> Objects.equals(type, e.getType()));
    }

}
