package com.starcloud.ops.business.product.api.sku.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 下单限制配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLimitConfig {

    @Schema(description = "是否限制新用户")
    private Boolean isNewUser;
    /**
     * 限制的优惠券模板编号
     */
    @Schema(description = "是否仅限制下列优惠券下单")
    private List<Long> limitCouponTemplateId;
}
