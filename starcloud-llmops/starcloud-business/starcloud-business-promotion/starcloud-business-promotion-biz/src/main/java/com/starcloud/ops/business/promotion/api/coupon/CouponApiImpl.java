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
     * @param templateId 优惠券模板 ID
     * @param userIds    用户编号
     */
    @Override
    public void addCoupon(Long templateId, Set<Long> userIds) {
        couponService.takeCouponByAdmin(templateId, userIds);
    }

    /**
     * 【会员领取】领取优惠劵
     *
     * @param templateId 优惠券模板 ID
     * @param userId     用户编号
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
     * @param userId      用户编号
     * @param price       商品价格
     * @param spuIds      商品 spuID
     * @param categoryIds 商品分类ID
     * @return 符合的数量
     */
    @Override
    public Integer getMatchCouponCount(Long userId, Integer price, List<Long> spuIds, List<Long> skuIds, List<Long> categoryIds) {
        return couponService.getMatchCouponCount(userId, price, spuIds, skuIds, categoryIds);
    }

    /**
     * @param userId     用户编号
     * @param templateId 优惠券模板 ID
     * @return 符合的数组
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

    /**
     * @param userId     用户编号
     * @param templateId 优惠券模板 ID
     * @return Boolean
     */
    @Override
    public Boolean validateUserExitTemplateId(Long userId, List<Long> templateId) {
        List<CouponDO> couponDOList = couponService.getTakeListByTemplateId(userId, templateId);
        return !couponDOList.isEmpty();
    }


}
