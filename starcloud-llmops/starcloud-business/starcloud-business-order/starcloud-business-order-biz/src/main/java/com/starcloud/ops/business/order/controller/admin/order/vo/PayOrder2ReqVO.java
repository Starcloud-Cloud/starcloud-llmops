package com.starcloud.ops.business.order.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Schema(description = "管理后台 - 支付订单提交 Request VO")
@Data
public class PayOrder2ReqVO {

    @Schema(description = "支付单编号", required = true, example = "1024")
    @NotNull(message = "支付单编号不能为空")
    private String code;
}
