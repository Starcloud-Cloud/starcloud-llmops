package com.starcloud.ops.business.order.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 支付单创建 Request DTO
 */
@Data
public class PayOrderCreateReqDTO implements Serializable {

    public static final int SUBJECT_MAX_LENGTH = 32;
    /**
     * 应用编号
     */
    @NotNull(message = "应用编号不能为空")
    private Long appId;
    /**
     * 用户 IP
     */
    @NotEmpty(message = "用户 IP 不能为空")
    private String userIp;


    // ========== 商户相关字段 ==========

    /**
     * 商户订单编号
     */
    @NotEmpty(message = "商户订单编号不能为空")
    private String merchantOrderId;

    /**
     * 商户订单编号
     */
    @Schema(description ="商户签约ID" )
    private Long signId;
    /**
     * 商品标题
     */
    @NotEmpty(message = "商品标题不能为空")
    @Length(max = 32, message = "商品标题不能超过 32")
    private String subject;

    /**
     * 商品code
     */
    @NotEmpty(message = "商品Code不能为空")
    private String productCode;
    /**
     * 商品描述
     */
    @NotEmpty(message = "商品描述信息不能为空")
    @Length(max = 128, message = "商品描述信息长度不能超过128")
    private String body;

    // ========== 订单相关字段 ==========

    /**
     * 支付金额，单位：分
     */
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0", inclusive = false, message = "支付金额必须大于零")
    private Integer amount;

    /**
     * 支付过期时间
     */
    @NotNull(message = "支付过期时间不能为空")
    private LocalDateTime expireTime;

    @Schema(description = "优惠代码")
    private String discountCode;

    @Schema(description = "优惠代码ID")
    private Long discountId;

    /**
     * 用户 IP
     */
    private String userId;

        /**
     * 支付金额，单位：分
     */
    @DecimalMin(value = "0", inclusive = false, message = "支付金额必须大于零")
    private Integer price;



    //=================NEW =========================

//    public static final int SUBJECT_MAX_LENGTH = 32;

//    /**
//     * 应用编号
//     */
//    @NotNull(message = "应用编号不能为空")
//    private Long appId;
//    /**
//     * 用户 IP
//     */
//    @NotEmpty(message = "用户 IP 不能为空")
//    private String userIp;
//
//    // ========== 商户相关字段 ==========
//
//    /**
//     * 商户订单编号
//     */
//    @NotEmpty(message = "商户订单编号不能为空")
//    private String merchantOrderId;
//    /**
//     * 商品标题
//     */
//    @NotEmpty(message = "商品标题不能为空")
//    @Length(max = SUBJECT_MAX_LENGTH, message = "商品标题不能超过 32")
//    private String subject;
//    /**
//     * 商品描述
//     */
//    @Length(max = 128, message = "商品描述信息长度不能超过128")
//    private String body;
//
//    // ========== 订单相关字段 ==========
//
//    /**
//     * 支付金额，单位：分
//     */
//    @NotNull(message = "支付金额不能为空")
//    @DecimalMin(value = "0", inclusive = false, message = "支付金额必须大于零")
//    private Integer price;
//
//    /**
//     * 支付过期时间
//     */
//    @NotNull(message = "支付过期时间不能为空")
//    private LocalDateTime expireTime;
}
