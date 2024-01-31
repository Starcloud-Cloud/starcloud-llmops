package com.starcloud.ops.business.user.controller.admin.invite;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.user.api.SendUserMsgService;
import com.starcloud.ops.business.user.dal.dataobject.invite.AdminUserInviteDO;
import com.starcloud.ops.business.user.service.invite.AdminUserInviteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 邀请记录")
@RestController
@RequestMapping("/llm/invitation-records")
@Validated
@Slf4j
public class AdminUserInviteController {

    @Resource
    private AdminUserInviteService adminUserInviteService;

    @Resource
    private UserBenefitsService benefitsService;

    @Resource
    private SendUserMsgService sendUserMsgService;


    @PostMapping("/create/{inviterId}/{inviteeId}")
    @Operation(summary = "创建邀请记录")
    // @PreAuthorize("@ss.hasPermission('llm:invitation-records:create')")
    public CommonResult<Boolean> createInvitationRecords(@PathVariable("inviterId") Long inviterId,
                                                         @PathVariable("inviteeId") Long inviteeId) {
        adminUserInviteService.createInvitationRecords(inviterId, inviteeId);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得邀请记录")
    // @PreAuthorize("@ss.hasPermission('llm:invitation-records:query')")
    public CommonResult<Boolean> getInvitationRecords() {
        Long inviteUserId = SecurityFrameworkUtils.getLoginUserId();
        // 获取当天的邀请记录
        List<AdminUserInviteDO> todayInvitations = adminUserInviteService.getTodayInvitations(inviteUserId);
        if (todayInvitations.size() % 2 == 0) {
            log.info("用户【{}】已经邀请了【{}】人，开始赠送额外的权益", todayInvitations.size(), todayInvitations.size());
            benefitsService.addUserBenefitsByStrategyType(BenefitsStrategyTypeEnums.USER_INVITE_REPEAT.getName(), inviteUserId);
            sendUserMsgService.sendMsgToWx(inviteUserId, String.format(
                    "您已成功邀请了【%s】位朋友加入魔法AI大家庭，并成功解锁了一份独特的权益礼包【送3000字】" + "\n" + "\n" + "我们已经将这份珍贵的礼物送至您的账户中。" + "\n" + "\n" +
                            "值得一提的是，每邀请三位朋友，您都将再次解锁一个全新的权益包，彰显您的独特地位。", todayInvitations.size()));
        }
        return success(true);
    }


}
