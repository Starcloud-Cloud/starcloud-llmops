package com.starcloud.ops.business.promotion.api.coupon.dto;

import com.starcloud.ops.business.promotion.enums.common.PromotionDiscountTypeEnum;
import com.starcloud.ops.business.promotion.enums.coupon.CouponStatusEnum;
import com.starcloud.ops.business.promotion.enums.coupon.CouponTakeTypeEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠劵 Response DTO
 *
 * @author 芋道源码
 */
@Data
public class CouponTemplateRespDTO {

    // ========== 基本信息 BEGIN ==========
    /**
     * 优惠劵编号
     */
    private Long id;
    /**
     * 优惠劵名
     */
    private String name;
    /**
     * 优惠码状态
     *
     * 枚举 {@link CouponStatusEnum}
     */
    private Integer status;

    // ========== 基本信息 END ==========

    // ========== 领取情况 BEGIN ==========
    /**
     * 领取类型
     *
     * 枚举 {@link CouponTakeTypeEnum}
     */
    private Integer takeType;
    // ========== 领取情况 END ==========

    // ========== 使用规则 BEGIN ==========
    /**
     * 是否设置满多少金额可用，单位：分
     */
    private Integer usePrice;

    /**
     * 商品范围
     */
    private Integer productScope;
    /**
     * 商品范围编号的数组
     */
    private List<Long> productScopeValues;
    // ========== 使用规则 END ==========

    // ========== 使用效果 BEGIN ==========
    /**
     * 折扣类型
     */
    private Integer discountType;
    /**
     * 折扣百分比
     */
    private Integer discountPercent;
    /**
     * 优惠金额，单位：分
     */
    private Integer discountPrice;
    /**
     * 折扣上限，仅在 {@link #discountType} 等于 {@link PromotionDiscountTypeEnum#PERCENT} 时生效
     */
    private Integer discountLimitPrice;
    // ========== 使用效果 END ==========

    // ========== 使用情况 BEGIN ==========
    /**
     * 使用订单号
     */
    private Long useOrderId;
    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    // ========== 使用情况 END ==========
}
