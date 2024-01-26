package com.starcloud.ops.business.user.controller.admin.invitationrecords;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.user.api.SendUserMsgService;
import com.starcloud.ops.business.user.dal.dataobject.invitation.InvitationRecordsDO;
import com.starcloud.ops.business.user.service.InvitationRecordsService;
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
public class InvitationRecordsController {

    @Resource
    private InvitationRecordsService invitationRecordsService;

    @Resource
    private UserBenefitsService benefitsService;

    @Resource
    private SendUserMsgService sendUserMsgService;


    @PostMapping("/create/{inviterId}/{inviteeId}")
    @Operation(summary = "创建邀请记录")
    // @PreAuthorize("@ss.hasPermission('llm:invitation-records:create')")
    public CommonResult<Boolean> createInvitationRecords(            @PathVariable("inviterId") Long inviterId,
                                                                  @PathVariable("inviteeId") Long inviteeId) {
        invitationRecordsService.createInvitationRecords(inviterId,inviteeId);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得邀请记录")
    // @PreAuthorize("@ss.hasPermission('llm:invitation-records:query')")
    public CommonResult<Boolean> getInvitationRecords() {
        Long inviteUserId = SecurityFrameworkUtils.getLoginUserId();
        // 获取当天的邀请记录
        List<InvitationRecordsDO> todayInvitations = invitationRecordsService.getTodayInvitations(inviteUserId);
        if (todayInvitations.size() % 2 == 0) {
            log.info("用户【{}】已经邀请了【{}】人，开始赠送额外的权益",todayInvitations.size() , todayInvitations.size());
            benefitsService.addUserBenefitsByStrategyType(BenefitsStrategyTypeEnums.USER_INVITE_REPEAT.getName(), inviteUserId);
            sendUserMsgService.sendMsgToWx(inviteUserId, String.format(
                    "您已成功邀请了【%s】位朋友加入魔法AI大家庭，并成功解锁了一份独特的权益礼包【送3000字】" +"\n"+ "\n" +"我们已经将这份珍贵的礼物送至您的账户中。" + "\n"+"\n" +
                            "值得一提的是，每邀请三位朋友，您都将再次解锁一个全新的权益包，彰显您的独特地位。", todayInvitations.size()));
        }
        return success(true);
    }
    //
    // @GetMapping("/list")
    // @Operation(summary = "获得邀请记录列表")
    // @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    // @PreAuthorize("@ss.hasPermission('llm:invitation-records:query')")
    // public CommonResult<List<InvitationRecordsRespVO>> getInvitationRecordsList(@RequestParam("ids") Collection<Long> ids) {
    //     List<InvitationRecordsDO> list = invitationRecordsService.getInvitationRecordsList(ids);
    //     return success(InvitationRecordsConvert.INSTANCE.convertList(list));
    // }
    //
    // @GetMapping("/page")
    // @Operation(summary = "获得邀请记录分页")
    // @PreAuthorize("@ss.hasPermission('llm:invitation-records:query')")
    // public CommonResult<PageResult<InvitationRecordsRespVO>> getInvitationRecordsPage(@Valid InvitationRecordsPageReqVO pageVO) {
    //     PageResult<InvitationRecordsDO> pageResult = invitationRecordsService.getInvitationRecordsPage(pageVO);
    //     return success(InvitationRecordsConvert.INSTANCE.convertPage(pageResult));
    // }
    //

}
