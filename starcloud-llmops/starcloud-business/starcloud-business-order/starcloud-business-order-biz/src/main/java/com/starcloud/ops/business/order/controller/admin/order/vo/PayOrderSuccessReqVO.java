package com.starcloud.ops.business.order.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = " 获取支付订单状态")
@Data
public class PayOrderSuccessReqVO {


    @Schema(description = "订单编号")
    @NotEmpty(message = "订单编号不能为空")
    private String orderId;

}
