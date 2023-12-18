package com.starcloud.ops.business.user.service.impl;

import cn.hutool.extra.servlet.ServletUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.util.validation.ValidationUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
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
import com.starcloud.ops.business.user.pojo.dto.UserDTO;
import com.starcloud.ops.business.user.service.CommunicationService;
import com.starcloud.ops.business.user.service.StarUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;
import static cn.iocoder.yudao.module.system.enums.sms.SmsSceneEnum.*;
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

    @Resource
    private PasswordEncoder passwordEncoder;

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
    @DataPermission(enable = false)
    public void sendCode(CodeSendReqVO reqVO) {
        checkAccount(reqVO.getAccount());
        validatePhoneNum(reqVO);
        smsCodeApi.sendSmsCode(SmsConvert.INSTANCE.smsVo2SendDTO(reqVO).setCreateIp(getClientIP()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataPermission(enable = false)
    public void validateCode(CodeValidateReqVO reqVO) {
        checkAccount(reqVO.getAccount());
        if (SmsSceneEnum.ADMIN_MEMBER_BIND.getScene().equals(reqVO.getScene())) {
            AdminUserDO userByMobile = adminUserService.getUserByMobile(reqVO.getAccount());
            if (userByMobile != null) {
                throw exception(USER_MOBILE_EXISTS);
            }
        }
        smsCodeApi.useSmsCode(SmsConvert.INSTANCE.smsVo2UseDTO(reqVO, ADMIN_MEMBER_BIND.getScene(), getClientIP()));
        // 更新手机号
        UserProfileUpdateReqVO userUpdateReqVO = new UserProfileUpdateReqVO();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        userUpdateReqVO.setMobile(reqVO.getAccount());
        adminUserService.updateUserProfile(loginUserId, userUpdateReqVO);
    }

    public static String getClientIP() {
        HttpServletRequest request = WebFrameworkUtils.getRequest();
        if (request == null) {
            return null;
        }
        return ServletUtil.getClientIP(request);
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
    public AuthLoginRespVO codeRegister(CodeRegisterReqVO reqVO) {
        checkAccount(reqVO.getAccount());
        validateUserNameAndMobile(reqVO.getUsername(), reqVO.getAccount());
        smsCodeApi.useSmsCode(SmsConvert.INSTANCE.convert(reqVO, ADMIN_MEMBER_REGISTER.getScene(), getClientIP()));

        UserDTO userDTO = UserDTO.builder().username(reqVO.getUsername())
                .email(StringUtils.EMPTY)
                .password(passwordEncoder.encode(reqVO.getPassword()))
                .parentDeptId(2L)
                .userStatus(CommonStatusEnum.ENABLE.getStatus())
                .mobile(reqVO.getAccount())
                .build();

        Long userId = starUserService.createNewUser(userDTO);
        starUserService.addInviteBenefits(userId, reqVO.getInviteCode());
        // 登录
        return starUserService.createTokenAfterLoginSuccess(userId, reqVO.getAccount(), LoginLogTypeEnum.LOGIN_MOBILE);
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

    private void validatePhoneNum(CodeSendReqVO reqVO) {
        // 手机号绑定 手机号注册
        if (ADMIN_MEMBER_BIND.getScene().equals(reqVO.getScene())
                || ADMIN_MEMBER_REGISTER.getScene().equals(reqVO.getScene())) {
            AdminUserDO userByMobile = adminUserService.getUserByMobile(reqVO.getAccount());
            if (userByMobile != null) {
                throw exception(USER_MOBILE_EXISTS);
            }
        } else if (ADMIN_MEMBER_LOGIN.getScene().equals(reqVO.getScene())) {
            // 登录
            AdminUserDO userByMobile = adminUserService.getUserByMobile(reqVO.getAccount());
            if (userByMobile == null) {
                throw exception(SMS_SEND_MOBILE_NOT_EXISTS);
            }
        } else {
            throw exception(new ErrorCode(500, "不支持的短信场景，{}"), reqVO.getScene());
        }
    }

}
