package com.starcloud.ops.business.user.service.user.handler;

import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;

/**
 * 用户注册特殊逻辑处理器 handler 接口
 * 提供用户注册生命周期钩子接口；用户注册、邀请处理、权益发送、消息通知
 *
 * @author HUIHUI
 */
public interface NewUserHandler {


    /**
     * 用户等级
     */
    int USER_LEVEL = 1;
    /**
     * 用户权益
     */
    int USER_RIGHTS = 2;

    /**
     * 邀请人处理
     */
    int USER_INVITE = 10;

    /**
     * 用户优惠券
     */
    int USER_COUPON = 11;


    /**
     * 用户注册后处理逻辑
     *
     * @param adminUserDO  新注册用户信息
     * @param inviteUserDO 邀请人信息
     */
    default void afterUserRegister(AdminUserDO adminUserDO, AdminUserDO inviteUserDO) {
    }
}
