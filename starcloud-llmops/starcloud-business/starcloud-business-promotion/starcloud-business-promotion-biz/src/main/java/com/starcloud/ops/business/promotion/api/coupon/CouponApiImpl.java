package com.starcloud.ops.business.promotion.api.coupon;


import com.starcloud.ops.business.promotion.api.coupon.dto.CouponRespDTO;
import com.starcloud.ops.business.promotion.api.coupon.dto.CouponUseReqDTO;
import com.starcloud.ops.business.promotion.api.coupon.dto.CouponValidReqDTO;
import com.starcloud.ops.business.promotion.convert.coupon.CouponConvert;
import com.starcloud.ops.business.promotion.dal.dataobject.coupon.CouponDO;
import com.starcloud.ops.business.promotion.service.coupon.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 优惠劵 API 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class CouponApiImpl implements CouponApi {

    @Resource
    private CouponService couponService;

    @Override
    public void useCoupon(CouponUseReqDTO useReqDTO) {
        couponService.useCoupon(useReqDTO.getId(), useReqDTO.getUserId(),
                useReqDTO.getOrderId());
    }

    @Override
    public void returnUsedCoupon(Long id) {
        couponService.returnUsedCoupon(id);
    }

    @Override
    public CouponRespDTO validateCoupon(CouponValidReqDTO validReqDTO) {
        CouponDO coupon = couponService.validCoupon(validReqDTO.getId(), validReqDTO.getUserId());
        return CouponConvert.INSTANCE.convert(coupon);
    }

    /**
     * 【管理员发放】领取优惠劵
     *
     * @param templateId
     * @param userIds
     * @return 优惠劵
     */
    @Override
    public void addCoupon(Long templateId, Set<Long> userIds) {
        couponService.takeCouponByAdmin(templateId, userIds);
    }

    /**
     * 【会员领取】领取优惠劵
     *
     * @param templateId
     * @param userId
     * @return 优惠劵
     */
    @Override
    public void addCoupon(Long templateId, Long userId) {
        couponService.takeCouponByUser(templateId, userId);
    }

    @Override
    public void takeCouponByRegister(Long userId) {
        couponService.takeCouponByRegister(userId);
    }

    /**
     * @param userId
     * @param price
     * @param spuIds
     * @param categoryIds
     * @return
     */
    @Override
    public Integer getMatchCouponCount(Long userId, Integer price, List<Long> spuIds, List<Long> categoryIds) {
        return couponService.getMatchCouponCount(userId, price, spuIds, categoryIds);
    }

    /**
     * @param userId
     * @param templateId
     * @return
     */
    @Override
    public List<CouponRespDTO> getMatchCouponByTemplateId(Long userId, Long templateId) {
        List<CouponDO> couponDOList = couponService.getTakeListByTemplateId(userId, templateId);
        return CouponConvert.INSTANCE.convertList02(couponDOList);
    }

    /**
     * 获取优惠劵
     *
     * @param couponId 使用请求
     */
    @Override
    public CouponRespDTO getCoupon(Long couponId, Long userId) {

        CouponDO coupon = couponService.validCoupon(couponId, userId);
        return CouponConvert.INSTANCE.convert(coupon);
    }


}
