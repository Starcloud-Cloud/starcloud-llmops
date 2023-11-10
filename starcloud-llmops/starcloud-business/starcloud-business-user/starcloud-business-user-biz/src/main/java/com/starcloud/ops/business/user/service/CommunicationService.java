package com.starcloud.ops.business.user.service;

import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeLoginReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeRegisterReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeSendReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeValidateReqVO;

public interface CommunicationService {

    Integer getTypeCode();

    void checkAccount(String account);

    /**
     * 发送验证码
     * @param reqVO
     */
    void sendCode(CodeSendReqVO reqVO);

    /**
     * 验证码绑定当前用户
     * @param reqVO
     */
    void validateCode(CodeValidateReqVO reqVO);

    /**
     * 验证码登录
     * @param reqVO
     * @return
     */
    AuthLoginRespVO codeLogin(CodeLoginReqVO reqVO);

    /**
     * 验证码注册
     * @param reqVO
     */
    AuthLoginRespVO codeRegister(CodeRegisterReqVO reqVO);
}
