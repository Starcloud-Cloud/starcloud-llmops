package com.starcloud.ops.business.promotion.api.promocode;

/**
 * 兑换码 API 接口
 *
 * @author 芋道源码
 */
public interface PromoCodeApi {

    /**
     * 【会员】使用优惠码
     *
     * @param code
     * @param userId
     * @return 优惠劵
     */
    Long usePromoCode(String code, Long userId);


    /**
     * 【会员】获取优惠码下的优惠券Id
     *
     * @return 优惠劵
     */
    Long getPromoCodeTemplate( String code);
}
