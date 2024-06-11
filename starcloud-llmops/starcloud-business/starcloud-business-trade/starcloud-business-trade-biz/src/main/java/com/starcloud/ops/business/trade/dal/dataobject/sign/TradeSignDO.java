package com.starcloud.ops.business.trade.dal.dataobject.sign;

import cn.iocoder.yudao.framework.common.enums.TerminalEnum;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.product.api.spu.dto.SubscribeConfigDTO;
import com.starcloud.ops.business.trade.dal.dataobject.brokerage.BrokerageUserDO;
import com.starcloud.ops.business.trade.dal.dataobject.delivery.DeliveryExpressDO;
import com.starcloud.ops.business.trade.dal.dataobject.delivery.DeliveryPickUpStoreDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.trade.enums.delivery.DeliveryTypeEnum;
import com.starcloud.ops.business.trade.enums.order.TradeOrderCancelTypeEnum;
import com.starcloud.ops.business.trade.enums.order.TradeOrderRefundStatusEnum;
import com.starcloud.ops.business.trade.enums.order.TradeOrderTypeEnum;
import com.starcloud.ops.business.trade.enums.sign.TradeSignStatusEnum;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易订阅 DO
 *
 * @author 芋道源码
 */
@TableName(value = "trade_sign", autoResultMap = true)
@KeySequence("trade_sign_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeSignDO extends TenantBaseDO {

    /**
     * 发货物流公司编号 - 空（无需发货）
     */
    public static final Long LOGISTICS_ID_NULL = 0L;

    /**
     *  扣款延迟时间
     */
    public static final Integer PAY_DELAY_NUM = 4;

    // ========== 签约单基本信息 ==========
    /**
     * 签约编号，主键自增
     */
    private Long id;
    /**
     * 订阅流水号
     * <p>
     * 例如说，1146347329394184195
     */
    private String no;

    /**
     * 订单类型
     * <p>
     * 枚举 {@link TradeOrderTypeEnum}
     */
    private Integer type;
    /**
     * 签约记录单来源
     * <p>
     * 枚举 {@link TerminalEnum}
     */
    private Integer terminal;
    /**
     * 用户编号
     * <p>
     * 关联 MemberUserDO 的 id 编号
     */
    private Long userId;
    /**
     * 用户 IP
     */
    private String userIp;

    /**
     * 创建来源
     */
    private String signFrom;
    /**
     * 用户备注
     */
    private String userRemark;
    /**
     * 订阅状态
     * <p>
     * 枚举 {@link TradeSignStatusEnum}
     */
    private Integer status;
    /**
     * 购买的商品数量
     */
    private Integer productCount;

    /**
     * 首次签约完成时间
     */
    private LocalDateTime finishTime;
    /**
     * 签约取消时间
     */
    private LocalDateTime cancelTime;
    /**
     * 取消类型
     * <p>
     * 枚举 {@link TradeOrderCancelTypeEnum}
     */
    private Integer cancelType;
    /**
     * 商家备注
     */
    private String remark;
    /**
     * 是否评价
     * <p>
     * true - 已评价
     * false - 未评价
     */
    private Boolean commentStatus;

    /**
     * 推广人编号
     * <p>
     * 关联 {@link BrokerageUserDO#getId()} 字段，即 { @link MemberUserRespDTO#getId()} 字段
     */
    private Long brokerageUserId;

    /**
     * 支付签约编号
     * <p>
     * 对接 pay-module-biz 支付服务的支付签约编号，即 PayOrderDO 的 id 编号
     */
    private Long paySignId;
    /**
     * 是否已支付
     * <p>
     * true - 已经支付过
     * false - 没有支付过
     */
    private Boolean paySignStatus;

    /**
     * 是否已支付
     * <p>
     * true - 已经支付过
     * false - 没有支付过
     */
    private LocalDate payTime;
    /**
     * 支付渠道
     * <p>
     * 对应 PayChannelEnum 枚举
     */
    private String payChannelCode;

    /**
     * 商品原价，单位：分
     * <p>
     * totalPrice = {@link TradeOrderItemDO#getPrice()} * {@link TradeOrderItemDO#getCount()} 求和
     * <p>
     * 对应 taobao 的 trade.total_fee 字段
     */
    private Integer totalPrice;
    /**
     * 优惠金额，单位：分
     * <p>
     * 对应 taobao 的 order.discount_fee 字段
     */
    private Integer discountPrice;
    /**
     * 运费金额，单位：分
     */
    private Integer deliveryPrice;
    /**
     * 签约调价，单位：分
     * <p>
     * 正数，加价；负数，减价
     */
    private Integer adjustPrice;
    /**
     * 应付金额（总），单位：分
     * <p>
     * = {@link #totalPrice}
     * - {@link #couponPrice}
     * - {@link #discountPrice}
     * + {@link #adjustPrice}
     */
    private Integer signPrice;

    // ========== 收件 + 物流基本信息 ==========
    /**
     * 配送方式
     * <p>
     * 枚举 {@link DeliveryTypeEnum}
     */
    private Integer deliveryType;
    /**
     * 发货物流公司编号
     * <p>
     * 如果无需发货，则 logisticsId 设置为 0。原因是，不想再添加额外字段
     * <p>
     * 关联 {@link DeliveryExpressDO#getId()}
     */
    private Long logisticsId;
    /**
     * 发货物流单号
     * <p>
     * 如果无需发货，则 logisticsNo 设置 ""。原因是，不想再添加额外字段
     */
    private String logisticsNo;
    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;
    /**
     * 收件人名称
     */
    private String receiverName;
    /**
     * 收件人手机
     */
    private String receiverMobile;
    /**
     * 收件人地区编号
     */
    private Integer receiverAreaId;
    /**
     * 收件人详细地址
     */
    private String receiverDetailAddress;
    /**
     * 自提门店编号
     * <p>
     * 关联 {@link DeliveryPickUpStoreDO#getId()}
     */
    private Long pickUpStoreId;
    /**
     * 自提核销码
     */
    private String pickUpVerifyCode;

    // ========== 售后基本信息 ==========
    /**
     * 售后状态
     * <p>
     * 枚举 {@link TradeOrderRefundStatusEnum}
     */
    private Integer refundStatus;
    /**
     * 退款金额，单位：分
     * <p>
     * 注意，退款并不会影响 {@link #signPrice} 实际支付金额
     * 也就说，一个签约最终产生多少金额的收入 = payPrice - refundPrice
     */
    private Integer refundPrice;

    // ========== 营销基本信息 ==========
    /**
     * 优惠劵编号
     */
    private Long couponId;
    /**
     * 优惠劵减免金额，单位：分
     * <p>
     * 对应 taobao 的 trade.coupon_fee 字段
     */
    private Integer couponPrice;

    /**
     * 使用的积分
     */
    private Integer usePoint;
    /**
     * 积分抵扣的金额，单位：分
     * <p>
     * 对应 taobao 的 trade.point_fee 字段
     */
    private Integer pointPrice;
    /**
     * 赠送的积分
     */
    private Integer givePoint;
    /**
     * 退还的使用的积分
     */
    private Integer refundPoint;
    /**
     * VIP 减免金额，单位：分
     */
    private Integer vipPrice;

    /**
     * 秒杀活动编号
     * <p>
     * 关联 SeckillActivityDO 的 id 字段
     */
    private Long seckillActivityId;

    /**
     * 砍价活动编号
     * <p>
     * 关联 BargainActivityDO 的 id 字段
     */
    private Long bargainActivityId;
    /**
     * 砍价记录编号
     * <p>
     * 关联 BargainRecordDO 的 id 字段
     */
    private Long bargainRecordId;

    /**
     * 拼团活动编号
     * <p>
     * 关联 CombinationActivityDO 的 id 字段
     */
    private Long combinationActivityId;
    /**
     * 拼团团长编号
     * <p>
     * 关联 CombinationRecordDO 的 headId 字段
     */
    private Long combinationHeadId;
    /**
     * 拼团记录编号
     * <p>
     * 关联 CombinationRecordDO 的 id 字段
     */
    private Long combinationRecordId;

    // ========== 权益相关字段 =========
    /**
     * 属性，JSON 格式
     */
    @TableField(typeHandler = GiveRightsDTOTypeHandler.class)
    private List<AdminUserRightsAndLevelCommonDTO> giveRights;


    public static class GiveRightsDTOTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseArray(json, AdminUserRightsAndLevelCommonDTO.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }


    /**
     * 属性，JSON 格式
     */
    @TableField(typeHandler = SubscribeConfigDTOTypeHandler.class)
    private SubscribeConfigDTO signConfigs;


    public static class SubscribeConfigDTOTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseObject(json, SubscribeConfigDTO.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }


}
