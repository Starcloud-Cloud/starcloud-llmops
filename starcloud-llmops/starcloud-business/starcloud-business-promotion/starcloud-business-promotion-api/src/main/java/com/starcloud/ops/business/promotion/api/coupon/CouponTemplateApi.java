package com.starcloud.ops.business.promotion.api.coupon;

import com.starcloud.ops.business.promotion.api.coupon.dto.CouponRespDTO;
import com.starcloud.ops.business.promotion.api.coupon.dto.CouponTemplateRespDTO;
import com.starcloud.ops.business.promotion.api.coupon.dto.CouponUseReqDTO;
import com.starcloud.ops.business.promotion.api.coupon.dto.CouponValidReqDTO;

import javax.validation.Valid;
import java.util.Set;

/**
 * 优惠劵 API 接口
 *
 * @author 芋道源码
 */
public interface CouponTemplateApi {

    /**
     * 使用优惠劵
     *
     * @param useReqDTO 使用请求
     */
    CouponTemplateRespDTO getCouponTemplate(Long id);

}
