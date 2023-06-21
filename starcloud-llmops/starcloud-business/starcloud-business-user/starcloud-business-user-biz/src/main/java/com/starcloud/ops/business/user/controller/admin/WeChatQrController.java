package com.starcloud.ops.business.user.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.QrCodeTicketVO;
import com.starcloud.ops.business.user.pojo.request.ScanLoginRequest;
import com.starcloud.ops.business.user.service.WeChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.UN_AUTH_ERROR;

@Tag(name = "星河云海 - 微信公众号二维码登录")
@RestController
@RequestMapping("/llm/wechat")
public class WeChatQrController {


    @Autowired
    private WeChatService weChatService;

    @Operation(summary = "获取公共号二维码")
    @GetMapping("/qr")
    @PermitAll
    public CommonResult<QrCodeTicketVO> qrCodeCreate() {
        return CommonResult.success(weChatService.qrCodeCreate());
    }

    @Operation(summary = "获取公共号二维码")
    @PostMapping("/qr/login")
    @PermitAll
    public CommonResult<AuthLoginRespVO> qrLogin(@RequestBody @Valid ScanLoginRequest request) {
        Long userId = weChatService.authUser(request);
        if (userId == null || userId <= 0) {
            return CommonResult.error(UN_AUTH_ERROR);
        }
        return CommonResult.success(weChatService.createTokenAfterLoginSuccess(userId));
    }

}
