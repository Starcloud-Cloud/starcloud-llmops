package com.starcloud.ops.business.trade.dal.dataobject.sign;

import cn.iocoder.yudao.framework.common.enums.TerminalEnum;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.starcloud.ops.business.product.api.spu.dto.GiveRightsDTO;
import com.starcloud.ops.business.trade.dal.dataobject.brokerage.BrokerageUserDO;
import com.starcloud.ops.business.trade.dal.dataobject.delivery.DeliveryPickUpStoreDO;
import com.starcloud.ops.business.trade.dal.dataobject.order.TradeOrderItemDO;
import com.starcloud.ops.business.trade.enums.order.TradeOrderCancelTypeEnum;
import com.starcloud.ops.business.trade.enums.order.TradeOrderRefundStatusEnum;
import com.starcloud.ops.business.trade.enums.order.TradeOrderTypeEnum;
import com.starcloud.ops.business.trade.enums.sign.TradeSignStatusEnum;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 交易订阅 DO
 *
 * @author 芋道源码
 */
@TableName(value ="trade_sign", autoResultMap = true)
@KeySequence("trade_sign_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeSignDO extends BaseDO {

    /**
     * 发货物流公司编号 - 空（无需发货）
     */
    public static final Long LOGISTICS_ID_NULL = 0L;

    // ========== 签约单基本信息 ==========
    /**
     * 签约编号，主键自增
     */
    private Long id;
    /**
     * 订阅流水号
     *
     * 例如说，1146347329394184195
     */
    private String no;

    /**
     * 订单类型
     *
     * 枚举 {@link TradeOrderTypeEnum}
     */
    private Integer type;
    /**
     * 签约记录单来源
     *
     * 枚举 {@link TerminalEnum}
     */
    private Integer terminal;
    /**
     * 用户编号
     *
     * 关联 MemberUserDO 的 id 编号
     */
    private Long userId;
    /**
     * 用户 IP
     */
    private String userIp;

    /**
     *  创建来源
     */
    private String signFrom;
    /**
     * 用户备注
     */
    private String userRemark;
    /**
     * 订阅状态
     *
     * 枚举 {@link TradeSignStatusEnum}
     */
    private Integer status;
    /**
     * 购买的商品数量
     */
    private Integer productCount;
    /**
     * 取消类型
     *
     * 枚举 {@link TradeOrderCancelTypeEnum}
     */
    private Integer cancelType;
    /**
     * 商家备注
     */
    private String remark;
    /**
     * 是否评价
     *
     * true - 已评价
     * false - 未评价
     */
    private Boolean commentStatus;

    /**
     * 推广人编号
     *
     * 关联 {@link BrokerageUserDO#getId()} 字段，即 { @link MemberUserRespDTO#getId()} 字段
     */
    private Long brokerageUserId;



    /**
     * 支付签约编号
     *
     * 对接 pay-module-biz 支付服务的支付签约编号，即 PayOrderDO 的 id 编号
     */
    private Long paySignId;
    /**
     * 是否已支付
     *
     * true - 已经支付过
     * false - 没有支付过
     */
    private Boolean paySignStatus;

    /**
     * 付款时间
     */
    private LocalDate payTime;




    /**
     * 首次签约完成时间
     */
    private LocalDateTime finishTime;
    /**
     * 签约取消时间
     */
    private LocalDateTime cancelTime;




    /**
     * 运费金额，单位：分
     */
    private Integer deliveryPrice;
    /**
     * 支付渠道
     *
     * 对应 PayChannelEnum 枚举
     */
    private String payChannelCode;

    /**
     * 商品原价，单位：分
     *
     * totalPrice = {@link TradeOrderItemDO#getPrice()} * {@link TradeOrderItemDO#getCount()} 求和
     *
     * 对应 taobao 的 trade.total_fee 字段
     */
    private Integer totalPrice;
    /**
     * 优惠金额，单位：分
     *
     * 对应 taobao 的 order.discount_fee 字段
     */
    private Integer discountPrice;
    /**
     * 签约调价，单位：分
     *
     * 正数，加价；负数，减价
     */
    private Integer adjustPrice;
    /**
     * 应付金额（总），单位：分
     *
     * = {@link #totalPrice}
     * - {@link #couponPrice}
     * - {@link #discountPrice}
     * + {@link #adjustPrice}
     */
    private Integer signPrice;




    /**
     * 自提门店编号
     *
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
     *
     * 枚举 {@link TradeOrderRefundStatusEnum}
     */
    private Integer refundStatus;
    /**
     * 退款金额，单位：分
     *
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
     *
     * 对应 taobao 的 trade.coupon_fee 字段
     */
    private Integer couponPrice;

    // ========== 权益相关字段 =========
    /**
     * 属性，JSON 格式
     */
    @TableField(typeHandler = GiveRightsDTOTypeHandler.class)
    private List<GiveRightsDTO> giveRights;

    public static class GiveRightsDTOTypeHandler extends AbstractJsonTypeHandler<Object> {

        @Override
        protected Object parse(String json) {
            return JsonUtils.parseArray(json, GiveRightsDTO.class);
        }

        @Override
        protected String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }



}
