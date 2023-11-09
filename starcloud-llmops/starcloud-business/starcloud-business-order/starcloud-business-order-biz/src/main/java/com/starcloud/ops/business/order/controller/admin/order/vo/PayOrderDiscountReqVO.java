package com.starcloud.ops.business.order.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 支付订单提交 Request VO")
@Data
public class PayOrderDiscountReqVO {

    @Schema(description = "用户选择的产品 code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "产品编号不能为空")
    private String productCode;


    @Schema(description = "用户未选择对应类型的产品 code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "产品noNeed不能为空")
    private String noNeedProductCode;


    @Schema(description = "优惠码")
    private String discountCode;
}
