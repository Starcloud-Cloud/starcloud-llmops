package com.starcloud.ops.business.user.controller.admin.invite;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.user.api.SendUserMsgService;
import com.starcloud.ops.business.user.controller.admin.invite.vo.rule.AdminUserInviteRuleCreateReqVO;
import com.starcloud.ops.business.user.dal.dataobject.invite.AdminUserInviteDO;
import com.starcloud.ops.business.user.service.invite.AdminUserInviteRuleService;
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
@RequestMapping("/llm/invite/rule")
@Validated
@Slf4j
public class AdminUserInviteRuleController {

    @Resource
    private AdminUserInviteRuleService adminUserInviteRuleService;



    @PostMapping("/create")
    @Operation(summary = "创建邀请记录")
    // @PreAuthorize("@ss.hasPermission('llm:invitation-records:create')")
    public CommonResult<Boolean> createInvitationRecords(@RequestBody AdminUserInviteRuleCreateReqVO createReqVO) {
        adminUserInviteRuleService.createRule(createReqVO);
        return success(true);
    }

}
