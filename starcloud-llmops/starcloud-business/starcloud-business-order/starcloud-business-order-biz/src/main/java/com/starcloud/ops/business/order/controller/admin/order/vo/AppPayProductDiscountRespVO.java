package com.starcloud.ops.business.order.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 支付订单详细信息 Response VO")
@Data
@ToString(callSuper = true)
public class AppPayProductDiscountRespVO {


    @Schema(description = "商品编号")
    private String code;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "原始金额，单位：分")
    private Long originalAmount;

    @Schema(description = "优惠金额，单位：分")
    private Long discountAmount;

    @Schema(description = "优惠后金额，单位：分")
    private Long discountedAmount;

    @Schema(description = "折扣券状态")
    private Boolean discountCouponStatus;

    @Schema(description = "折扣券状态")
    private String discountCouponName;

}
