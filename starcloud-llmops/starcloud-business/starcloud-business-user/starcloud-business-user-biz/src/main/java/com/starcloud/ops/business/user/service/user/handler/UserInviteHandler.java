package com.starcloud.ops.business.user.service.user.handler;

import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.common.TimeRangeTypeEnum;
import com.starcloud.ops.business.user.api.rights.dto.AddRightsDTO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.service.invite.AdminUserInviteRuleService;
import com.starcloud.ops.business.user.service.invite.AdminUserInviteService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 新用户邀请人权益发放的 {@link UserRegisterHandler} 实现类
 *
 * @author Cusack Alan
 */
@Slf4j
@Component
@Order(UserRegisterHandler.INVITE_HANDLER)
public class UserInviteHandler implements UserRegisterHandler {


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

        if (Objects.isNull(inviteUserDO)) {
            log.info("【afterUserRegister】邀请人信息和权益设置，当前用户不存在邀请人，跳过邀请人设置");
            return;
        }

        log.info("【afterUserRegister】邀请人信息注入，创建邀请记录");
        // 增加邀请记录
        Long invitationId = adminUserInviteService.createInvitationRecords(inviteUserDO.getId(), adminUserDO.getId());
        log.info("【afterUserRegister】邀请人信息设置成功,开始准备邀请人权益发放，准备邀请人发放权益");

        AddRightsDTO inviteUserRightsDTO = new AddRightsDTO()
                .setUserId(inviteUserDO.getId())
                .setMagicBean(AdminUserRightsBizTypeEnum.USER_INVITE.getMagicBean())
                .setMagicImage(AdminUserRightsBizTypeEnum.USER_INVITE.getMagicImage())
                .setMatrixBean(AdminUserRightsBizTypeEnum.USER_INVITE.getMatrixBean())
                .setTimeNums(1)
                .setTimeRange(TimeRangeTypeEnum.MONTH.getType())
                .setBizId(String.valueOf(invitationId))
                .setBizType(AdminUserRightsBizTypeEnum.USER_INVITE.getType())
                .setLevelId(null);

        adminUserRightsService.createRights(inviteUserRightsDTO);
        log.info("【afterUserRegister】邀请人信息设置成功,基础权益人发放完成");

        adminUserInviteService.setInviteRights(inviteUserDO, invitationId);
        log.info("【afterUserRegister】邀请人权益发放，邀请人权益发放成功");

    }
}
