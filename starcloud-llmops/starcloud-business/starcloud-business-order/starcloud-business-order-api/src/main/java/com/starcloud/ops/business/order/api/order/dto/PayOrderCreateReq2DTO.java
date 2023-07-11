package com.starcloud.ops.business.order.api.order.dto;

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
    @NotEmpty(message = "商品code不能为空")
    private String productCode;

}
