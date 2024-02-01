package com.starcloud.ops.business.promotion.api.coupon;

import com.starcloud.ops.business.promotion.api.coupon.dto.CouponRespDTO;
import com.starcloud.ops.business.promotion.api.coupon.dto.CouponUseReqDTO;
import com.starcloud.ops.business.promotion.api.coupon.dto.CouponValidReqDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * 优惠劵 API 接口
 *
 * @author 芋道源码
 */
public interface CouponApi {

    /**
     * 使用优惠劵
     *
     * @param useReqDTO 使用请求
     */
    void useCoupon(@Valid CouponUseReqDTO useReqDTO);

    /**
     * 退还已使用的优惠券
     *
     * @param id 优惠券编号
     */
    void returnUsedCoupon(Long id);

    /**
     * 校验优惠劵
     *
     * @param validReqDTO 校验请求
     * @return 优惠劵
     */
    CouponRespDTO validateCoupon(@Valid CouponValidReqDTO validReqDTO);


    /**
     * 【管理员发放】领取优惠劵
     *
     * @param templateId 校验请求
     * @param userIds    校验请求
     * @return 优惠劵
     */
    void addCoupon(Long templateId, Set<Long> userIds);

    /**
     * 【会员领取】领取优惠劵
     *
     * @param templateId
     * @param userId
     * @return 优惠劵
     */
    void addCoupon(Long templateId, Long userId);

    /**
     * 发送新人优惠券
     *
     * @param userId
     */
    void takeCouponByRegister(Long userId);

    Integer getMatchCouponCount(Long userId, Integer price, List<Long> spuIds, List<Long> categoryIds);

    List<CouponRespDTO> getMatchCouponByTemplateId(Long userId, Long templateId);

    /**
     * 获取优惠劵
     *
     * @param couponId 使用请求
     */
    CouponRespDTO getCoupon(Long couponId, Long userId);


}
