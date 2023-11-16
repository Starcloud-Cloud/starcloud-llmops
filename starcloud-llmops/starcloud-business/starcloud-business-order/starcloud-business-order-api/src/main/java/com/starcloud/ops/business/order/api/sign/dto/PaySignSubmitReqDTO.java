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
public class PaySignSubmitReqDTO implements Serializable {
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
    private String merchantSignId;

    /**
     * 渠道代码
     */
    @Schema(description = "渠道代码")
    @NotEmpty(message = "渠道代码")
    private String channelCode;


    // ========== 签约相关字段 ==========
    @Schema(description = "周期类型 ，枚举值为 DAY 和 MONTH")
    @NotEmpty(message = "周期类型")
    private String periodType;

    @Schema(description = "周期数")
    @NotEmpty(message = "周期数 不能为空")
    private String period;

    @Schema(description = "发起首次扣款的时间")
    @NotEmpty(message = "发起首次扣款的时间 不能为空")
    private String executeTime;

    @Schema(description = "单次扣款最大金额")
    @NotEmpty(message = "单次扣款最大金额")
    private String singleAmount;

    @Schema(description = "周期内允许扣款的总金额。")
    private String totalAmount;

    @Schema(description = "总扣款次数")
    private String totalPayments;

}
