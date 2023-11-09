package com.starcloud.ops.business.order.api.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 支付单创建 Request DTO
 */
@Data
public class PayOrderCreateReq2DTO implements Serializable {

    /**
     * 商品code
     */
    @Schema(description = "商品code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "商品code不能为空")
    private String productCode;

    @Schema(description = "优惠代码")
    private String discountCode;
    /**
     * 时间戳
     */
    @Schema(description = "时间戳", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "时间戳不能为空")
    private Long timestamp;


}
