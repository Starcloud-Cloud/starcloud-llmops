package com.starcloud.ops.business.user.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.UserRoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.dept.DeptMapper;
import cn.iocoder.yudao.module.system.dal.mysql.permission.UserRoleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.mq.producer.permission.PermissionProducer;
import cn.iocoder.yudao.module.system.service.mail.MailSendServiceImpl;
import com.starcloud.ops.business.user.dal.dataObject.RecoverPasswordDO;
import com.starcloud.ops.business.user.dal.dataObject.RegisterUserDO;
import com.starcloud.ops.business.user.dal.mysql.RecoverPasswordMapper;
import com.starcloud.ops.business.user.dal.mysql.RegisterUserMapper;
import com.starcloud.ops.business.user.pojo.request.ChangePasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RecoverPasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RegisterRequest;
import com.starcloud.ops.business.user.service.LlmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.*;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.*;

@Service
public class LlmUserServiceImpl implements LlmUserService {

    @Autowired
    private AdminUserMapper adminUserMapper;

    @Autowired
    private MailSendServiceImpl mailSendService;

    @Autowired
    private RegisterUserMapper registerUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private RecoverPasswordMapper recoverPasswordMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PermissionProducer permissionProducer;

    @Override
    public boolean register(RegisterRequest request) {
        validateEmailAndUsername(request.getUsername(), request.getEmail());
        String activationCode = IdUtil.getSnowflakeNextIdStr();
        RegisterUserDO registerUserDO = RegisterUserDO.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(0)
                .activationCode(activationCode)
                .registerDate(LocalDateTime.now())
                .registerIp(ServletUtils.getClientIP()).build();

        String url = getUrl();
        String activationUrl = url + "/admin-api/llm/auth/activation/" + activationCode + "?redirectUri=" + getReferer() + "login" ;
        Map<String, Object> map = new HashMap<>();
        map.put("activationUrl", activationUrl);
        mailSendService.sendSingleMail(request.getEmail(), 1L, UserTypeEnum.ADMIN.getValue(), "register_temp", map);
        int insert = registerUserMapper.insert(registerUserDO);
        return insert > 0;
    }

    private String getReferer() {
        HttpServletRequest servletRequest = ServletUtils.getRequest();
        return servletRequest.getHeader("Referer");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activation(String activationCode) {
        RegisterUserDO registerUserDO = registerUserMapper.selectByActivationCode(activationCode);
        if (registerUserDO == null) {
            throw exception(ACTIVATION_CODE_ERROR);
        }
        if (registerUserDO.getRegisterDate().compareTo(LocalDateTime.now().minusMinutes(30)) < 0) {
            registerUserDO.setStatus(2);
            registerUserMapper.updateById(registerUserDO);
            throw exception(OPERATE_TIME_OUT);
        }
        DeptDO deptDO = new DeptDO();
        deptDO.setParentId(2L);
        deptDO.setName(registerUserDO.getUsername() + "_dept");
        deptDO.setEmail(registerUserDO.getEmail());
        deptDO.setStatus(0);
        deptDO.setTenantId(2L);
        deptMapper.insert(deptDO);

        Long deptId = deptDO.getId();
        AdminUserDO userDO = new AdminUserDO();
        userDO.setDeptId(deptId);
        userDO.setUsername(registerUserDO.getUsername());
        userDO.setEmail(registerUserDO.getEmail());
        userDO.setStatus(0);
        userDO.setNickname(registerUserDO.getUsername());
        userDO.setPassword(registerUserDO.getPassword());
        userDO.setTenantId(2L);
        adminUserMapper.insert(userDO);

        UserRoleDO userRoleDO = new UserRoleDO();
        userRoleDO.setRoleId(2L);
        userRoleDO.setUserId(userDO.getId());
        userRoleDO.setCreator(userDO.getUsername());
        userRoleDO.setUpdater(userDO.getUpdater());
        userRoleDO.setTenantId(userDO.getTenantId());
        userRoleMapper.insert(userRoleDO);

        registerUserDO.setStatus(1);
        registerUserMapper.updateById(registerUserDO);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                permissionProducer.sendUserRoleRefreshMessage();
            }

        });
        return true;
    }

    @Override
    public boolean checkCode(String verificationCode) {
        RecoverPasswordDO recoverPasswordDO = recoverPasswordMapper.selectByCode(verificationCode);
        if (recoverPasswordDO == null) {
            throw exception(ACTIVATION_CODE);
        }
        if (recoverPasswordDO.getRecoverDate().compareTo(LocalDateTime.now().minusMinutes(30)) < 0) {
            recoverPasswordDO.setStatus(2);
            recoverPasswordMapper.updateById(recoverPasswordDO);
            throw exception(OPERATE_TIME_OUT);
        }
        return true;
    }

    @Override
    public boolean recoverPassword(RecoverPasswordRequest request) {
        AdminUserDO userDO = adminUserMapper.selectByEmail(request.getEmail());
        if (userDO == null) {
            throw exception(USER_NOT_EXISTS);
        }
        validateEmail(request.getEmail());
        String recoverCode = IdUtil.getSnowflakeNextIdStr();
        String url = getReferer();
        String recoverUrl = url +  "pages/reset-password/reset-password2?verificationCode=" + recoverCode;

        Map<String, Object> map = new HashMap<>();
        map.put("recoverUrl", recoverUrl);
        mailSendService.sendSingleMail(request.getEmail(), userDO.getId(), UserTypeEnum.ADMIN.getValue(), "recover_temp", map);

        RecoverPasswordDO recoverPasswordDO = new RecoverPasswordDO();
        recoverPasswordDO.setUserId(userDO.getId());
        recoverPasswordDO.setRecoverCode(recoverCode);
        recoverPasswordDO.setRecoverDate(LocalDateTime.now());
        recoverPasswordDO.setRecoverIp(ServletUtils.getClientIP());
        recoverPasswordDO.setEmail(request.getEmail());
        recoverPasswordDO.setStatus(0);
        recoverPasswordMapper.insert(recoverPasswordDO);
        return true;
    }

    @Override
    public boolean changePassword(ChangePasswordRequest request) {
        RecoverPasswordDO recoverPasswordDO = recoverPasswordMapper.selectByCode(request.getVerificationCode());
        if (recoverPasswordDO == null) {
            throw exception(ACTIVATION_CODE);
        }
        if (recoverPasswordDO.getRecoverDate().compareTo(LocalDateTime.now().minusMinutes(30)) < 0) {
            recoverPasswordDO.setStatus(2);
            recoverPasswordMapper.updateById(recoverPasswordDO);
            throw exception(OPERATE_TIME_OUT);
        }
        AdminUserDO userDO = adminUserMapper.selectById(recoverPasswordDO.getUserId());
        userDO.setPassword(passwordEncoder.encode(request.getNewPassword()));
        adminUserMapper.updateById(userDO);
        return true;
    }

    private String getUrl() {
        HttpServletRequest servletRequest = ServletUtils.getRequest();
        String scheme = servletRequest.getScheme();
        String serverName = servletRequest.getServerName();
        int port = servletRequest.getServerPort();
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(serverName);
        } catch (UnknownHostException e) {
            throw exception(IP_CONVERT_ERROR);
        }
        if (inetAddress.getHostAddress().equals(serverName) || inetAddress.isLoopbackAddress()) {
            return scheme + "://" + serverName + ":" + port;
        } else {
            return scheme + "://" + serverName;
        }
    }


    private void validateEmail(String email) {
        RecoverPasswordDO recoverPasswordDO = recoverPasswordMapper.selectByEmail(email);
        if (recoverPasswordDO != null && recoverPasswordDO.getRecoverDate().compareTo(LocalDateTime.now().minusMinutes(30)) > 0) {
            throw exception(REPEAT_RECOVER);
        }
    }

    private void validateEmailAndUsername(String username, String email) {
        DataPermissionUtils.executeIgnore(() -> {
            AdminUserDO user = adminUserMapper.selectByUsername(username);
            if (user != null) {
                throw exception(USER_USERNAME_EXISTS);
            }
            user = adminUserMapper.selectByEmail(email);
            if (user != null) {
                throw exception(USER_EMAIL_EXISTS);
            }

            RegisterUserDO registerUserDO = registerUserMapper.selectByUsername(username);
            // 注册时间30分钟内
            if (registerUserDO != null && registerUserDO.getRegisterDate().compareTo(LocalDateTime.now().minusMinutes(30)) > 0) {
                throw exception(USER_USERNAME_EXISTS);
            }
            registerUserDO = registerUserMapper.selectByEmail(email);
            if (registerUserDO != null && registerUserDO.getRegisterDate().compareTo(LocalDateTime.now().minusMinutes(30)) > 0) {
                throw exception(USER_EMAIL_EXISTS);
            }

        });
    }
}
