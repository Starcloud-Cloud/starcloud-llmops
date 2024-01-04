package com.starcloud.ops.business.promotion.api.promocode;


import com.starcloud.ops.business.promotion.dal.dataobject.promocode.PromoCodeTemplateDO;
import com.starcloud.ops.business.promotion.enums.common.PromotionCodeTypeEnum;
import com.starcloud.ops.business.promotion.service.promocode.PromoCodeService;
import com.starcloud.ops.business.promotion.service.promocode.PromoCodeTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 优惠劵 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class PromoCodeApiImpl implements PromoCodeApi {


    @Resource
    private PromoCodeService promoCodeService;


    @Resource
    private PromoCodeTemplateService promoCodeTemplateService;


    /**
     * 【会员】使用优惠码
     *
     * @param code
     * @param userId
     * @return 优惠劵
     */
    @Override
    public Long usePromoCode(String code, Long userId) {
        return promoCodeService.useCouponPromoCode(code,userId);
    }

    /**
     * 【会员】获取优惠码下的优惠券详情
     *
     * @param code
     * @return 优惠劵
     */
    @Override
    public Long getPromoCodeTemplate(String code) {
        PromoCodeTemplateDO template = promoCodeTemplateService.getTemplate(code, PromotionCodeTypeEnum.COUPON_CODE.getType());
        if (Objects.nonNull(template)){
            return template.getCouponTemplateId();
        }
        return 0L;
    }
}
