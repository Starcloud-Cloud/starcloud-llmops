package com.starcloud.ops.business.order.api.sign.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * 支付单创建 Request DTO
 */
@Data
public class PaySignCreateReqDTO implements Serializable {


    /**
     * 应用编号
     */
    // @NotNull(message = "应用编号不能为空")
    private Long appId;

    /**
     * 用户 IP
     */
    // @NotEmpty(message = "用户 IP 不能为空")
    private String userIp;
    /**
     * 用户 IP
     */
    @NotEmpty(message = " 产品编号")
    private String productCode;

    // ========== 商户相关字段 ==========

    /**
     * 商户订单编号
     */
    // @NotEmpty(message = "商户订单编号不能为空")
    private String merchantSignId;

}
