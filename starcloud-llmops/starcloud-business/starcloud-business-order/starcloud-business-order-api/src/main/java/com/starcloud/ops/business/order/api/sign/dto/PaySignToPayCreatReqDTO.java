package com.starcloud.ops.business.order.api.sign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 支付单创建 Request DTO
 */
@Data
public class PaySignToPayCreatReqDTO implements Serializable {
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
    @NotEmpty(message = "商户签约编号不能为空")
    private String merchantOrderId;

    /**
     * 渠道代码
     */
    @Schema(description = "渠道代码")
    @NotEmpty(message = "渠道代码")
    private String channelCode;

    // ========== 签约相关字段 ==========
    /**
     * 签约 ID
     */
    @NotEmpty(message = "签约 ID")
    private Long signId;

    @Schema(description = "商品code")
    @NotEmpty(message = "商品code不能为空")
    private String productCode;

    @Schema(description = "商品名称")
    @NotEmpty(message = "商品名称不能为空")
    private String subject;

    @Schema(description = "签约协议号")
    @NotEmpty(message = "签约协议号 不能为空")
    private String agreementNo;

    @Schema(description = "金额。")
    private Integer totalAmount;

    @Schema(description = "总扣款次数")
    private String totalPayments;

}
