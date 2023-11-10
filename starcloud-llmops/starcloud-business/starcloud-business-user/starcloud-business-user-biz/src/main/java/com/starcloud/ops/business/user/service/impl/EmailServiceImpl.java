package com.starcloud.ops.business.user.service.impl;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.profile.UserProfileUpdateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.user.vo.user.UserUpdateReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.framework.sms.SmsCodeProperties;
import cn.iocoder.yudao.module.system.service.mail.MailSendServiceImpl;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.controller.admin.vo.CodeLoginReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeRegisterReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeSendReqVO;
import com.starcloud.ops.business.user.controller.admin.vo.CodeValidateReqVO;
import com.starcloud.ops.business.user.enums.CommunicationToolsEnum;
import com.starcloud.ops.business.user.service.CommunicationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static cn.hutool.core.util.RandomUtil.randomInt;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.USER_EMAIL_EXISTS;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.USER_NOT_EXISTS;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.*;

@Service
@Slf4j
public class EmailServiceImpl implements CommunicationService {

    @Resource
    private MailSendServiceImpl mailSendService;

    @Resource
    private AdminUserService adminUserService;

    @Resource
    private SmsCodeProperties smsCodeProperties;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final Pattern pattern = Pattern.compile("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$");


    @Override
    public Integer getTypeCode() {
        return CommunicationToolsEnum.EMAIL.getCode();
    }

    @Override
    public void checkAccount(String email) {
        if (!pattern.matcher(email).matches()) {
            throw exception(INVALID_EMAIL);
        }
        AdminUserDO user = adminUserService.getUserByEmail(email);
        if (user != null) {
            throw exception(USER_EMAIL_EXISTS);
        }
    }

    @Override
    public void sendCode(CodeSendReqVO reqVO) {
        String email = reqVO.getAccount();
        checkAccount(email);
        Long userId = WebFrameworkUtils.getLoginUserId();
        if (userId == null) {
            throw exception(USER_NOT_EXISTS);
        }
        String code = String.valueOf(randomInt(smsCodeProperties.getBeginCode(), smsCodeProperties.getEndCode() + 1));
        redisTemplate.boundValueOps(email + "_code").set(code, 5, TimeUnit.MINUTES);
        Map<String, Object> map = new HashMap<>();
        map.put("verificationCode", code);
        mailSendService.sendSingleMail(email, userId, UserTypeEnum.ADMIN.getValue(), "bind_email_temp", map);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void validateCode(CodeValidateReqVO reqVO) {
        String email = reqVO.getAccount();
        checkAccount(email);
        String code = redisTemplate.boundValueOps(email + "_code").get();

        if (StringUtils.isBlank(code)) {
            throw exception(VERIFICATION_CODE_OVERDUE);
        }
        if (!reqVO.getCode().equalsIgnoreCase(code)) {
            throw exception(INVALID_VERIFICATION_CODE);
        }

        UserProfileUpdateReqVO userUpdateReqVO = new UserProfileUpdateReqVO();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        userUpdateReqVO.setEmail(reqVO.getAccount());
        adminUserService.updateUserProfile(loginUserId,userUpdateReqVO);
    }

    @Override
    public AuthLoginRespVO codeLogin(CodeLoginReqVO reqVO) {
        return null;
    }

    @Override
    public AuthLoginRespVO codeRegister(CodeRegisterReqVO reqVO) {
        return null;
    }
}
