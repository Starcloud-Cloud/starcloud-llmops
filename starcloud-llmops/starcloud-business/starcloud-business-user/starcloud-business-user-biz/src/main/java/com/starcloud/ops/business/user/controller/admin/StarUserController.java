package com.starcloud.ops.business.user.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.NotifyExpiringLevelRespVO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.NotifyExpiringRightsRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.AdminUserInfoRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.AdminUserNotifyExpiringRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.UserDetailVO;
import com.starcloud.ops.business.user.pojo.request.ChangePasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RecoverPasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RegisterRequest;
import com.starcloud.ops.business.user.pojo.request.UserProfileUpdateRequest;
import com.starcloud.ops.business.user.service.StarUserService;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@RestController
@RequestMapping("/llm/auth")
@Tag(name = "星河云海-用户管理")
public class StarUserController {

    @Autowired
    private StarUserService llmUserService;

    @Autowired
    private AdminUserLevelService adminUserLevelService;

    @Autowired
    private AdminUserRightsService adminUserRightsService;


    @PostMapping("/register")
    @PermitAll
    @Operation(summary = "邮箱注册帐号", description = "邮箱注册帐号")
    @TenantIgnore
    @OperateLog(enable = false)
    public CommonResult<Boolean> register(@RequestBody @Valid RegisterRequest request) {
        return CommonResult.success(llmUserService.register(request));
    }

    @GetMapping("/activation/{activationCode}")
    @PermitAll
    @Operation(summary = "激活链接", description = "激活链接")
    @TenantIgnore
    @OperateLog(enable = false)
    public void activation(@PathVariable String activationCode,
                           @RequestParam("redirectUri") String redirectUri,
                           HttpServletResponse resp) {
        boolean activation = llmUserService.activation(activationCode);
        if (activation) {
            try {
                resp.sendRedirect(redirectUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @PostMapping("/recover/password")
    @PermitAll
    @Operation(summary = "忘记密码邮箱修改", description = "忘记密码邮箱修改")
    @TenantIgnore
    @OperateLog(enable = false)
    public CommonResult<Boolean> recoverPassword(@RequestBody @Valid RecoverPasswordRequest request) {
        return CommonResult.success(llmUserService.recoverPassword(request));
    }

    @GetMapping("/recover/check/{verificationCode}")
    @PermitAll
    @Operation(summary = "验证code是否过期", description = "验证code是否过期")
    @TenantIgnore
    @OperateLog(enable = false)
    public CommonResult<Boolean> checkCode(@PathVariable("verificationCode") String verificationCode) {
        return CommonResult.success(llmUserService.checkCode(verificationCode));
    }

    @PostMapping("/change/password")
    @PermitAll
    @Operation(summary = "修改密码", description = "修改密码")
    @TenantIgnore
    @OperateLog(enable = false)
    public CommonResult<Boolean> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        return CommonResult.success(llmUserService.changePassword(request));
    }


    @GetMapping("/user/detail")
    @Operation(summary = "获取用户明细", description = "获取用户明细")
    public CommonResult<UserDetailVO> userDetail() {
        return CommonResult.success(llmUserService.userDetail());
    }

    @GetMapping("/user/all_detail")
    @Operation(summary = "获取用户明细", description = "获取用户明细")
    public CommonResult<AdminUserInfoRespVO> userRightsDetail() {
        return CommonResult.success(llmUserService.userDetail(getLoginUserId()));
    }

    @PutMapping("/user/update")
    @Operation(summary = "修改用户个人信息", description = "修改用户个人信息")
    @TenantIgnore
    public CommonResult<Boolean> updateUserProfile(@RequestBody @Valid UserProfileUpdateRequest request) {
        return CommonResult.success(llmUserService.updateUserProfile(request));
    }


    @PutMapping("/test/addBenefits")
    @Operation(summary = "测试添加注册权益", description = "测试添加注册权益")
    @TenantIgnore
    public CommonResult<Boolean> addBenefits(@RequestBody @Valid UserProfileUpdateRequest request) {
        llmUserService.addBenefits(getLoginUserId(),215L);
        return CommonResult.success(true);
    }

    @PutMapping("/test/notify_expiring")
    @Operation(summary = "用户过期提醒", description = "用户过期提醒")
    @TenantIgnore
    public CommonResult<AdminUserNotifyExpiringRespVO> NotifyExpiring(@RequestBody @Valid UserProfileUpdateRequest request) {
        AdminUserNotifyExpiringRespVO adminUserNotifyExpiringRespVO = new AdminUserNotifyExpiringRespVO();
        NotifyExpiringLevelRespVO notifyExpiringLevelRespVO = adminUserLevelService.notifyExpiringLevel(getLoginUserId());
        NotifyExpiringRightsRespVO notifyExpiringRightsRespVO = adminUserRightsService.notifyExpiringRights(getLoginUserId());
        adminUserNotifyExpiringRespVO.setNotifyExpiringLevelRespVO(notifyExpiringLevelRespVO);
        adminUserNotifyExpiringRespVO.setNotifyExpiringRightsRespVO(notifyExpiringRightsRespVO);
        return CommonResult.success(adminUserNotifyExpiringRespVO);
    }

}
