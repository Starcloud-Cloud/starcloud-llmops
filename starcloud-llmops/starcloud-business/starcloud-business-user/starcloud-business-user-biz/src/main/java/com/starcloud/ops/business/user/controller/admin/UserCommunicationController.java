package com.starcloud.ops.business.user.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeLoginReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeRegisterReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeSendReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeValidateReqVO;
import com.starcloud.ops.business.user.service.CommunicationService;
import com.starcloud.ops.business.user.service.factory.CommunicationToolsFactroy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/llm/communication")
@Tag(name = "星河云海-用户通讯工具管理")
public class UserCommunicationController {

    @PostMapping("/code/send")
    @Operation(summary = "发送手机验证码")
    @PermitAll
    public CommonResult<Boolean> sendCode(@RequestBody @Valid CodeSendReqVO reqVO) {
        getService(reqVO.getTool()).sendCode(reqVO);
        return success(true);
    }

    @PostMapping("/validate/code")
    @Operation(summary = "校验二维码绑定用户")
    public CommonResult<Boolean> validateCode(@RequestBody @Valid CodeValidateReqVO reqVO) {
        getService(reqVO.getTool()).validateCode(reqVO);
        return success(true);
    }

    @PostMapping("/code/login")
    @Operation(summary = "使用手机 + 验证码登录")
    @PermitAll
    public CommonResult<AuthLoginRespVO> codeLogin(@RequestBody @Valid CodeLoginReqVO reqVO) {
        return success(getService(reqVO.getTool()).codeLogin(reqVO));
    }

    @PostMapping("/code/register")
    @Operation(summary = "使用手机注册")
    @PermitAll
    public CommonResult<Boolean> codeRegister(@RequestBody @Valid CodeRegisterReqVO reqVO) {
        getService(reqVO.getTool()).codeRegister(reqVO);
        return success(true);
    }

    private CommunicationService getService(Integer code) {
        return CommunicationToolsFactroy.getService(code);
    }

}
