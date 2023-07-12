package com.starcloud.ops.business.order.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 支付订单详细信息 Response VO")
@Data
@ToString(callSuper = true)
public class AppPayOrderDetailsRespVO {


    @Schema(description = "订单编号")
    private String id;

    @Schema(description = "商户订单编号")
    private String merchantOrderId;

    @Schema(description = "商品标题")
    private String subject;

    @Schema(description = "商品描述")
    private String body;

    @Schema(description = "支付金额，单位：分")
    private Long amount;

    @Schema(description = "支付状态")
    private Integer status;

    // @NotNull(message = "退款状态不能为空")
    // private Integer refundStatus;
    //
    // @Schema(description = "退款次数", required = true)
    // @NotNull(message = "退款次数不能为空")
    // private Integer refundTimes;
    //
    // @Schema(description = "退款总金额，单位：分", required = true)
    // @NotNull(message = "退款总金额，单位：分不能为空")
    // private Long refundAmount;
}
