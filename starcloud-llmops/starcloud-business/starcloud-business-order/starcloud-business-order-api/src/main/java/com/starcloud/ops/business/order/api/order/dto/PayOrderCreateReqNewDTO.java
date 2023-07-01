package com.starcloud.ops.business.order.api.order.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付单创建 Request DTO
 */
@Data
public class PayOrderCreateReqNewDTO implements Serializable {

    /**
     * 用户 IP
     */
    @NotEmpty(message = "用户 IP 不能为空")
    private String userIp;

    // ========== 商品相关字段 ==========

    /**
     * 商品标题
     */
    @NotEmpty(message = "商品标题不能为空")
    @Length(max = 32, message = "商品标题不能超过 32")
    private String subject;
    /**
     * 商品描述-对应订单附加信息。
     */
    @NotEmpty(message = "商品描述信息不能为空")
    @Length(max = 128, message = "商品描述信息长度不能超过128")
    private String body;

    // ========== 订单相关字段 ==========

    /**
     * 订单总金额，单位：元
     */
    @NotNull(message = "商户订单号。")
    @Length(max = 64, message = "商户订单号不能超过 64")
    private Integer outTradeNo;

    /**
     * 订单总金额，单位：元
     */
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "支付金额必须大于零")
    private BigDecimal amount;

    /**
     * 支付过期时间
     */
    @NotNull(message = "支付过期时间不能为空")
    private LocalDateTime timeExpire;

    // ========== 扫码相关字段 ==========
    @NotNull(message = "支付过期时间不能为空")
    private LocalDateTime qrPayMode;



}
