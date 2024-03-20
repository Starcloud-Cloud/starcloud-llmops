package com.starcloud.ops.business.promotion.api.coupon.dto;

import lombok.Data;

import java.util.List;

/**
 * 优惠劵 Response DTO
 *
 * @author 芋道源码
 */
@Data
public class CouponProductRespDTO {

    /**
     * 商品 SPU 编号
     */
    private Long spuId;
    /**
     * 商品 SKU 编号
     */
    private List<Long> skuId;
}
