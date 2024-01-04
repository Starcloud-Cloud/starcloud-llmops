package com.starcloud.ops.business.promotion.api.coupon;


import com.starcloud.ops.business.promotion.api.coupon.dto.CouponTemplateRespDTO;
import com.starcloud.ops.business.promotion.convert.coupon.CouponTemplateConvert;
import com.starcloud.ops.business.promotion.dal.dataobject.coupon.CouponTemplateDO;
import com.starcloud.ops.business.promotion.service.coupon.CouponTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.promotion.enums.ErrorCodeConstants.PROMO_CODE_TEMPLATE_NOT_EXISTS;

/**
 * 优惠劵 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class CouponTemplateApiImpl implements CouponTemplateApi {

    @Resource
    private CouponTemplateService couponTemplateService;


    /**
     * 使用优惠劵
     *
     * @param id
     */
    @Override
    public CouponTemplateRespDTO getCouponTemplate(Long id) {
        CouponTemplateDO couponTemplate = couponTemplateService.getCouponTemplate(id);
        if (couponTemplate == null) {
            throw exception(PROMO_CODE_TEMPLATE_NOT_EXISTS);
        }
        return CouponTemplateConvert.INSTANCE.convert01(couponTemplate);
    }
}
