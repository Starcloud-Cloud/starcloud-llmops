package com.starcloud.ops.business.user.service.impl;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.util.validation.ValidationUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.sms.SmsCodeApi;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.user.UserUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.enums.logger.LoginLogTypeEnum;
import cn.iocoder.yudao.module.system.enums.sms.SmsSceneEnum;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.controller.admin.vo.CodeLoginReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeRegisterReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeSendReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeValidateReqVO;
import com.starcloud.ops.business.user.convert.SmsConvert;
import com.starcloud.ops.business.user.enums.CommunicationToolsEnum;
import com.starcloud.ops.business.user.service.CommunicationService;
import com.starcloud.ops.business.user.service.StarUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.INVALID_PHONE_NUMBER;

@Slf4j
@Service
public class MobilePhoneServiceImpl implements CommunicationService {

    @Resource
    private SmsCodeApi smsCodeApi;

    @Resource
    private AdminUserService adminUserService;

    @Resource
    private StarUserService starUserService;

    @Override
    public Integer getTypeCode() {
        return CommunicationToolsEnum.MOBILE_PHONE.getCode();
    }

    @Override
    public void checkAccount(String phoneNum) {
        boolean mobile = ValidationUtils.isMobile(phoneNum);
        if (!mobile) {
            throw exception(INVALID_PHONE_NUMBER);
        }
    }

    @Override
    public void sendCode(CodeSendReqVO reqVO) {
        checkAccount(reqVO.getAccount());
        smsCodeApi.sendSmsCode(SmsConvert.INSTANCE.smsVo2SendDTO(reqVO).setCreateIp(getClientIP()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void validateCode(CodeValidateReqVO reqVO) {
        checkAccount(reqVO.getAccount());
        smsCodeApi.useSmsCode(SmsConvert.INSTANCE.smsVo2UseDTO(reqVO, SmsSceneEnum.ADMIN_MEMBER_BIND.getScene()));
        // 更新手机号
        UserProfileUpdateReqVO userUpdateReqVO = new UserProfileUpdateReqVO();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        userUpdateReqVO.setMobile(reqVO.getAccount());
        adminUserService.updateUserProfile(loginUserId, userUpdateReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthLoginRespVO codeLogin(CodeLoginReqVO reqVO) {
        checkAccount(reqVO.getAccount());
        smsCodeApi.useSmsCode(SmsConvert.INSTANCE.convert(reqVO, SmsSceneEnum.ADMIN_MEMBER_LOGIN.getScene(), getClientIP()));
        AdminUserDO user = adminUserService.getUserByMobile(reqVO.getAccount());
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
        // 创建 Token 令牌，记录登录日志
        return starUserService.createTokenAfterLoginSuccess(user.getId(), reqVO.getAccount(), LoginLogTypeEnum.LOGIN_MOBILE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void codeRegister(CodeRegisterReqVO reqVO) {
        checkAccount(reqVO.getAccount());
        validateUserNameAndMobile(reqVO.getUsername(), reqVO.getAccount());
        smsCodeApi.useSmsCode(SmsConvert.INSTANCE.convert(reqVO, SmsSceneEnum.ADMIN_MEMBER_REGISTER.getScene(), getClientIP()));
        Long userId = starUserService.createNewUser(reqVO.getUsername(), null, reqVO.getPassword(), 2L, CommonStatusEnum.ENABLE.getStatus());
        starUserService.addInviteBenefits(userId, reqVO.getInviteCode());
    }

    private void validateUserNameAndMobile(String userName, String mobile) {
        AdminUserDO userByMobile = adminUserService.getUserByMobile(mobile);
        if (userByMobile != null) {
            throw exception(USER_MOBILE_EXISTS);
        }
        AdminUserDO userByUsername = adminUserService.getUserByUsername(userName);
        if (userByUsername != null) {
            throw exception(USER_USERNAME_EXISTS);
        }
    }

}
