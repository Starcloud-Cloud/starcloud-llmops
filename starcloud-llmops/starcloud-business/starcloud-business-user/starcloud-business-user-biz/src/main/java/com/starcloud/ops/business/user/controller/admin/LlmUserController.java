package com.starcloud.ops.business.user.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.starcloud.ops.business.user.pojo.request.ChangePasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RecoverPasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RegisterRequest;
import com.starcloud.ops.business.user.service.LlmUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/llm/auth")
@Tag(name = "注册")
public class LlmUserController {

    @Autowired
    private LlmUserService llmUserService;


    @PostMapping("/register")
    @PermitAll
    @Operation(summary = "邮箱注册帐号", description = "邮箱注册帐号")
    @TenantIgnore
    public CommonResult<Boolean> register(@RequestBody @Valid RegisterRequest request) {
        return CommonResult.success(llmUserService.register(request));
    }

    @GetMapping("/activation/{activationCode}")
    @PermitAll
    @Operation(summary = "激活链接", description = "激活链接")
    @TenantIgnore
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
    public CommonResult<Boolean> recoverPassword(@RequestBody @Valid RecoverPasswordRequest request) {
        return CommonResult.success(llmUserService.recoverPassword(request));
    }

    @GetMapping("/recover/check/{verificationCode}")
    @PermitAll
    @Operation(summary = "验证code是否过期", description = "验证code是否过期")
    @TenantIgnore
    public CommonResult<Boolean> checkCode(@PathVariable("verificationCode") String verificationCode) {
        return CommonResult.success(llmUserService.checkCode(verificationCode));
    }

    @PostMapping("/change/password")
    @PermitAll
    @Operation(summary = "修改密码", description = "修改密码")
    @TenantIgnore
    public CommonResult<Boolean> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        return CommonResult.success(llmUserService.changePassword(request));
    }

}
