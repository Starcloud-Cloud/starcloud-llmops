package com.starcloud.ops.business.user.service.user.handler;

import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.starcloud.ops.business.promotion.api.coupon.CouponApi;
import com.starcloud.ops.business.user.api.rights.dto.AddRightsDTO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.service.SendSocialMsgService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 新用户注册权益发送的 {@link UserRegisterHandler} 实现类
 *
 * @author Cusack Alan
 */
@Slf4j
@Component
@Order(UserRegisterHandler.USER_REGISTER_RIGHTS)
public class UserRegisterRightsHandler implements UserRegisterHandler{


    @Resource
    private AdminUserRightsService adminUserRightsService;


    @Resource
    private    CouponApi couponApi;

    @Resource
    private SendSocialMsgService sendSocialMsgService;

    /**
     * 用户注册后处理逻辑
     *
     * @param adminUserDO  新注册用户
     * @param inviteUserDO 邀请人信息
     */
    @Override
    public void afterUserRegister(AdminUserDO adminUserDO, AdminUserDO inviteUserDO) {
        log.info("【afterUserRegister】用户注册，准备新增用户注册权益");

        AddRightsDTO newUserRightsDTO = new AddRightsDTO()
                .setUserId(adminUserDO.getId())
                .setMagicBean(AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getMagicBean())
                .setMagicImage(AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getMagicImage())
                .setMatrixBean(AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getMatrixBean())
                .setTimeNums(1)
                .setTimeRange(TimeRangeTypeEnum.MONTH.getType())
                .setBizId(String.valueOf(adminUserDO.getId()))
                .setBizType(AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getType())
                .setLevelId(null);

        adminUserRightsService.createRights(newUserRightsDTO);
        // 发放新人优惠券
        couponApi.takeCouponByRegister(adminUserDO.getId());

//        sendSocialMsgService.sendInviteMsg(adminUserDO.getId());

        log.info("【afterUserRegister】用户注册，新增用户注册权益成功");
    }
}
