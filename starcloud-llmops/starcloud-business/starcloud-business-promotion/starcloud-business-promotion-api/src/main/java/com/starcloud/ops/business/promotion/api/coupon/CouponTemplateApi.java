package com.starcloud.ops.business.promotion.api.coupon;

import com.starcloud.ops.business.promotion.api.coupon.dto.CouponTemplateRespDTO;

/**
 * 优惠劵 API 接口
 *
 * @author 芋道源码
 */
public interface CouponTemplateApi {

    /**
     * 使用优惠劵
     *
     * @param id 使用请求
     */
    CouponTemplateRespDTO getCouponTemplate(Long id);

}
