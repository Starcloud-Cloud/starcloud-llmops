package com.starcloud.ops.business.user.service.user.handler;

import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.starcloud.ops.business.user.api.level.dto.OperateDTO;
import com.starcloud.ops.business.user.api.level.dto.UserLevelBasicDTO;
import com.starcloud.ops.business.user.api.rights.dto.AdminUserRightsAndLevelCommonDTO;
import com.starcloud.ops.business.user.api.rights.dto.TimesRangeDTO;
import com.starcloud.ops.business.user.api.rights.dto.UserRightsBasicDTO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.service.invite.AdminUserInviteService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 新用户邀请人权益发放的 {@link NewUserHandler} 实现类
 *
 * @author Cusack Alan
 */
@Slf4j
@Component
@Order(NewUserHandler.USER_INVITE)
public class NewUserInviteHandler implements NewUserHandler {


    @Resource
    private AdminUserRightsService adminUserRightsService;


    @Resource
    private AdminUserInviteService adminUserInviteService;


    /**
     * 用户注册后处理逻辑
     *
     * @param adminUserDO  新注册用户
     * @param inviteUserDO 邀请人信息
     */
    @Override
    public void afterUserRegister(AdminUserDO adminUserDO, AdminUserDO inviteUserDO) {
        log.info("【新用户注册 邀请人权益发放】，准备为邀请人发送邀请权益");

        // 判断是否存在邀请人
        if (Objects.isNull(inviteUserDO)) {
            log.info("【新用户注册 邀请人权益发放跳过】，当前注册用户未获取到邀请人信息，新用户信息为{}，邀请人信息为{}", JSONUtil.toJsonStr(adminUserDO), JSONUtil.toJsonStr((Object) null));
            return;
        }
        log.info("【新用户注册 邀请人权益开始发放】，当前注册用户获取到邀请人信息，新用户信息为{}，邀请人信息为{}", JSONUtil.toJsonStr(adminUserDO), JSONUtil.toJsonStr(inviteUserDO));

        // 增加邀请记录
        Long invitationId = adminUserInviteService.createInvitationRecords(inviteUserDO.getId(), adminUserDO.getId());
        log.info("【afterUserRegister】邀请人信息设置成功,开始准备邀请人权益发放，准备邀请人发放权益");


        AdminUserRightsBizTypeEnum bizTypeEnum = AdminUserRightsBizTypeEnum.USER_INVITE;


        int magicBean = bizTypeEnum.getMagicBean();
        int magicImage = bizTypeEnum.getMagicImage();
        int matrixBean = bizTypeEnum.getMatrixBean();
        if (TenantContextHolder.getTenantId() == 3) {
            magicBean = matrixBean * 1000;
            magicImage = matrixBean * 20;
        }


        AdminUserRightsAndLevelCommonDTO commonDTO = new AdminUserRightsAndLevelCommonDTO()
                .setRightsBasicDTO(new UserRightsBasicDTO()
                        .setMagicBean(magicBean)
                        .setMagicImage(magicImage)
                        .setMatrixBean(matrixBean)
                        .setTimesRange(new TimesRangeDTO().setNums(1).setRange(TimeRangeTypeEnum.MONTH.getType()))
                        .setOperateDTO(new OperateDTO().setIsAdd(true).setIsSuperposition(false)))
                .setLevelBasicDTO(new UserLevelBasicDTO()
                        .setLevelId(1L)
                        .setTimesRange(new TimesRangeDTO().setNums(99).setRange(TimeRangeTypeEnum.YEAR.getType()))
                        .setOperateDTO(new OperateDTO().setIsAdd(false).setIsSuperposition(false)));

        adminUserRightsService.createRights(commonDTO, inviteUserDO.getId(), bizTypeEnum.getType(), String.valueOf(invitationId));
        log.info("【afterUserRegister】邀请人信息设置成功,基础权益发放完成");


        // 必须在事务提交后，在发起任务，否则 邀请数据还没入库，就提前回调接入的业务
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                adminUserInviteService.setInviteRights(inviteUserDO, invitationId);
            }
        });


        log.info("【afterUserRegister】邀请人权益发放，邀请人权益发放成功");

    }
}
