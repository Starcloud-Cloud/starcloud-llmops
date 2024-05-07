package com.starcloud.ops.business.user.service.user.handler;

import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import com.starcloud.ops.business.promotion.api.coupon.CouponApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 新用户优惠券发放
 * {@link NewUserHandler} 实现类
 *
 * @author Cusack Alan
 */
@Slf4j
@Component
@Order(NewUserHandler.USER_COUPON)
public class NewUserCouponHandler implements NewUserHandler {


    @Resource
    private CouponApi couponApi;


    /**
     * 用户注册后处理逻辑
     *
     * @param adminUserDO  新注册用户
     * @param inviteUserDO 邀请人信息
     */
    @Override
    public void afterUserRegister(AdminUserDO adminUserDO, AdminUserDO inviteUserDO) {
        log.info("【新用户注册 新用户优惠券发放】，准备为新注册用户发送新用户优惠券");
        try {
            // 发放新人优惠券
            couponApi.takeCouponByRegister(adminUserDO.getId());
            log.info("【新用户注册 新用户优惠券发放成功】");
        } catch (RuntimeException e) {
            log.info("【新用户注册 新用户优惠券发放失败】", e);
            throw new RuntimeException("新用户优惠券发放失败");
        }
    }
}
