package com.starcloud.ops.business.order.api.sign.dto;

import com.starcloud.ops.business.order.enums.order.PayOrderStatusEnum;
import lombok.Data;

/**
 * 支付单信息 Response DTO
 * <p>
 * TODO 芋艿：还没定好字段
 *
 * @author 芋道源码
 */
@Data
public class PaySignRespDTO {

    /**
     * 订单编号，数据库自增
     */
    private Long id;
    /**
     * 渠道编码
     * <p>
     * 枚举 PayChannelEnum
     */
    private String channelCode;

    // ========== 商户相关字段 ==========
    /**
     * 商户订单编号
     * 例如说，内部系统 A 的订单号。需要保证每个 PayMerchantDO 唯一
     */
    private String merchantOrderId;

    // ========== 订单相关字段 ==========
    /**
     * 支付金额，单位：分
     */
    private Integer amount;
    /**
     * 支付状态
     * <p>
     * 枚举 {@link PayOrderStatusEnum}
     */
    private Integer status;


    // ========== 渠道相关字段 ==========

}
