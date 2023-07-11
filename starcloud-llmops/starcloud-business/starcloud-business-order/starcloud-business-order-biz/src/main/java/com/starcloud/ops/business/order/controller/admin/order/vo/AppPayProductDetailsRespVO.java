package com.starcloud.ops.business.order.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 支付订单详细信息 Response VO")
@Data
@ToString(callSuper = true)
public class AppPayProductDetailsRespVO {


    @Schema(description = "商品编号")
    private String code;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品图片")
    private String image;

    @Schema(description = "商品描述")
    private String describe;

    @Schema(description = "支付金额，单位：分")
    private Long amount;


}
