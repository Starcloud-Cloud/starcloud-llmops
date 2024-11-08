package com.starcloud.ops.business.user.service.user.handler;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.starcloud.ops.business.promotion.api.coupon.CouponApi;
import com.starcloud.ops.business.user.api.level.dto.OperateDTO;
import com.starcloud.ops.business.user.api.level.dto.UserLevelBasicDTO;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import com.starcloud.ops.business.user.api.rights.dto.TimesRangeDTO;
import com.starcloud.ops.business.user.api.rights.dto.UserRightsBasicDTO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.service.SendSocialMsgService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 邀请用户权益发放
 * {@link NewUserHandler} 实现类
 *
 * @author Cusack Alan
 */
@Slf4j
@Component
@Order(NewUserHandler.USER_RIGHTS)
public class NewUserRightHandler implements NewUserHandler {


    @Resource
    private AdminUserRightsService adminUserRightsService;


    @Resource
    private CouponApi couponApi;

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
        log.info("【新用户注册 新用户权益发放】，准备为邀请人发送邀请权益");

        AdminUserRightsBizTypeEnum bizTypeEnum;
        // 邀请人为空
        if (Objects.isNull(inviteUserDO)) {
            log.info("【新用户注册 邀请人权益发放跳过】，当前注册用户未获取到邀请人信息，新用户信息为{}，邀请人信息为{}", JSONUtil.toJsonStr(adminUserDO), JSONUtil.toJsonStr((Object) null));
            bizTypeEnum = AdminUserRightsBizTypeEnum.REGISTER;
        } else {
            log.info("【新用户注册 邀请人权益开始发放】，当前注册用户获取到邀请人信息，新用户信息为{}，邀请人信息为{}", JSONUtil.toJsonStr(adminUserDO), JSONUtil.toJsonStr(inviteUserDO));
            bizTypeEnum = AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER;
        }

        AdminUserRightsAndLevelCommonDTO commonDTO = new AdminUserRightsAndLevelCommonDTO();

        if (TenantContextHolder.getTenantId() == 3) {
            commonDTO.setRightsBasicDTO(new UserRightsBasicDTO()
                            .setMagicBean(99999)
                            .setMagicImage(99999)
                            .setMatrixBean(10)
                            .setTimesRange(new TimesRangeDTO().setNums(1).setRange(TimeRangeTypeEnum.MONTH.getType()))
                            .setOperateDTO(new OperateDTO().setIsAdd(true).setIsSuperposition(false)))
                    .setLevelBasicDTO(new UserLevelBasicDTO()
                            .setLevelId(1L)
                            .setTimesRange(new TimesRangeDTO().setNums(99).setRange(TimeRangeTypeEnum.YEAR.getType()))
                            .setOperateDTO(new OperateDTO().setIsAdd(true).setIsSuperposition(false)));
        } else {

            commonDTO.setRightsBasicDTO(new UserRightsBasicDTO()
                            .setMagicBean(bizTypeEnum.getMagicBean())
                            .setMagicImage(bizTypeEnum.getMagicImage())
                            .setMatrixBean(bizTypeEnum.getMatrixBean())
                            .setTimesRange(new TimesRangeDTO().setNums(1).setRange(TimeRangeTypeEnum.MONTH.getType()))
                            .setOperateDTO(new OperateDTO().setIsAdd(true).setIsSuperposition(false)))
                    .setLevelBasicDTO(new UserLevelBasicDTO()
                            .setLevelId(1L)
                            .setTimesRange(new TimesRangeDTO().setNums(99).setRange(TimeRangeTypeEnum.YEAR.getType()))
                            .setOperateDTO(new OperateDTO().setIsAdd(true).setIsSuperposition(false)));
        }

        adminUserRightsService.createRights(commonDTO, adminUserDO.getId(), bizTypeEnum.getType(), String.valueOf(adminUserDO.getId()));
        log.info("【afterUserRegister】用户注册，新增用户注册权益成功");
    }
}
