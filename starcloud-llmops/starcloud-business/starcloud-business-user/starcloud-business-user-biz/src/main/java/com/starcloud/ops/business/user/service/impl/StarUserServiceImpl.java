package com.starcloud.ops.business.user.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.UserRoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.dept.DeptMapper;
import cn.iocoder.yudao.module.system.dal.mysql.permission.RoleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.permission.UserRoleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.mq.producer.permission.PermissionProducer;
import cn.iocoder.yudao.module.system.service.mail.MailSendServiceImpl;
import com.starcloud.ops.business.limits.enums.BenefitsStrategyTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.user.controller.admin.vo.UserDetailVO;
import com.starcloud.ops.business.user.convert.UserConvert;
import com.starcloud.ops.business.user.convert.UserDetailConvert;
import com.starcloud.ops.business.user.dal.dataObject.RecoverPasswordDO;
import com.starcloud.ops.business.user.dal.dataObject.RegisterUserDO;
import com.starcloud.ops.business.user.dal.mysql.RecoverPasswordMapper;
import com.starcloud.ops.business.user.dal.mysql.RegisterUserMapper;
import com.starcloud.ops.business.user.pojo.request.ChangePasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RecoverPasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RegisterRequest;
import com.starcloud.ops.business.user.pojo.request.UserProfileUpdateRequest;
import com.starcloud.ops.business.user.service.StarUserService;
import com.starcloud.ops.business.user.util.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class StarUserServiceImpl implements StarUserService {

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

    @Autowired
    private UserBenefitsService benefitsService;

    @Autowired
    private RoleMapper roleMapper;

    @Value("${starcloud-llm.role.code:mofaai_free}")
    private String roleCode;

    @Value("${starcloud-llm.tenant.id:2}")
    private Long tenantId;


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

        if (request.getInviteCode() != null) {
            String userName = null;
            try {
                userName = EncryptionUtils.decryptString(request.getInviteCode());
                AdminUserDO userDO = adminUserMapper.selectByUsername(userName);
                if (userDO != null) {
                    registerUserDO.setInviteUserId(userDO.getId());
                }
            } catch (Exception e) {
                log.warn("计算邀请码异常", e);
            }
        }
        String url = getUrl();
        String activationUrl = url + "/admin-api/llm/auth/activation/" + activationCode + "?redirectUri=" + getOrigin() + "/login";
        Map<String, Object> map = new HashMap<>();
        map.put("activationUrl", activationUrl);
        mailSendService.sendSingleMail(request.getEmail(), 1L, UserTypeEnum.ADMIN.getValue(), "register_temp", map);
        int insert = registerUserMapper.insert(registerUserDO);
        return insert > 0;
    }

    private String getOrigin() {
        HttpServletRequest servletRequest = ServletUtils.getRequest();
        return servletRequest.getHeader("Origin");
    }

    private void addBenefits(Long currentUserId, Long inviteUserId) {
        try {
            if (inviteUserId != null && inviteUserId > 0) {
                //邀请注册权益 邀请人
                benefitsService.addUserBenefitsByStrategyType(BenefitsStrategyTypeEnums.USER_INVITE.getName(), inviteUserId);
                //被邀请人
                benefitsService.addUserBenefitsByStrategyType(BenefitsStrategyTypeEnums.INVITE_TO_REGISTER.getName(), currentUserId);
            } else {
                // 普通注册权益
                benefitsService.addUserBenefitsByStrategyType(BenefitsStrategyTypeEnums.SIGN_IN.getName(), currentUserId);
            }
        } catch (Exception e) {
            log.warn("新增权益失败，currentUserId={},inviteUserId={}", currentUserId, inviteUserId, e);
        }
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

        Long userId = createNewUser(registerUserDO.getUsername(), registerUserDO.getEmail(), registerUserDO.getPassword(), 2L);
        TenantContextHolder.setTenantId(tenantId);
        TenantContextHolder.setIgnore(false);
        addBenefits(userId, registerUserDO.getInviteUserId());
        TenantContextHolder.setIgnore(true);

        addBenefits(userId, registerUserDO.getInviteUserId());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                permissionProducer.sendUserRoleRefreshMessage();
            }

        });
        registerUserDO.setStatus(1);
        registerUserMapper.updateById(registerUserDO);
        return true;
    }

    @Override
    public Long createNewUser(String username, String email, String password, Long parentDeptId) {
        DeptDO deptDO = new DeptDO();
        deptDO.setParentId(parentDeptId);
        deptDO.setName(username + "_space");
        deptDO.setEmail(email);
        deptDO.setStatus(0);
        deptDO.setTenantId(tenantId);
        deptMapper.insert(deptDO);

        Long deptId = deptDO.getId();
        AdminUserDO userDO = new AdminUserDO();
        userDO.setDeptId(deptId);
        userDO.setUsername(username);
        userDO.setEmail(email);
        userDO.setStatus(0);
        userDO.setNickname(username);
        userDO.setPassword(password);
        userDO.setTenantId(tenantId);
        adminUserMapper.insert(userDO);

        RoleDO roleDO = roleMapper.selectByCode(roleCode,tenantId);
        if (roleDO == null) {
            throw exception(ROLE_NOT_EXIST);
        }

        UserRoleDO userRoleDO = new UserRoleDO();
        userRoleDO.setRoleId(roleDO.getId());
        userRoleDO.setUserId(userDO.getId());
        userRoleDO.setCreator(userDO.getUsername());
        userRoleDO.setUpdater(userDO.getUpdater());
        userRoleDO.setTenantId(userDO.getTenantId());
        userRoleMapper.insert(userRoleDO);
        return userDO.getId();
    }

    @Override
    public Boolean updateUserProfile(UserProfileUpdateRequest request) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            throw exception(AUTH_TOKEN_EXPIRED);
        }
        int length = request.getUsername().length();
        if (4 > length || length > 16 ) {
            throw exception(USERNAME_SIZE_ERROR);
        }

        DataPermissionUtils.executeIgnore(() ->{
            validateUserExists(userId);
            validateUserNameUnique(userId, request.getUsername());
            validateEmailUnique(userId, request.getEmail());
            validateMobileUnique(userId, request.getMobile());
        });
        adminUserMapper.updateById(UserConvert.INSTANCE.convert(request).setId(userId));
        return true;
    }

    void validateUserNameUnique(Long id, String username) {
        if (StrUtil.isBlank(username)) {
            return;
        }
        AdminUserDO user = adminUserMapper.selectByUsername(username);
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_USERNAME_EXISTS);
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_USERNAME_EXISTS);
        }
    }

    void validateEmailUnique(Long id, String email) {
        if (StrUtil.isBlank(email)) {
            return;
        }
        AdminUserDO user = adminUserMapper.selectByEmail(email);
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_EMAIL_EXISTS);
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_EMAIL_EXISTS);
        }
    }

    void validateUserExists(Long id) {
        if (id == null) {
            return;
        }
        AdminUserDO user = adminUserMapper.selectById(id);
        if (user == null) {
            throw exception(USER_NOT_EXISTS);
        }
    }

    void validateMobileUnique(Long id, String mobile) {
        if (StrUtil.isBlank(mobile)) {
            return;
        }
        AdminUserDO user = adminUserMapper.selectByMobile(mobile);
        if (user == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的用户
        if (id == null) {
            throw exception(USER_MOBILE_EXISTS);
        }
        if (!user.getId().equals(id)) {
            throw exception(USER_MOBILE_EXISTS);
        }
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
        String url = getOrigin();
        String recoverUrl = url + "/pages/reset-password/reset-password2?verificationCode=" + recoverCode;

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
    public UserDetailVO userDetail() {
        LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
        AdminUserDO userDO = adminUserMapper.selectById(loginUser.getId());
        String inviteCode = null;
        try {
            inviteCode = EncryptionUtils.encryptString(userDO.getUsername());
        } catch (Exception e) {
            throw exception(ENCRYPTION_ERROR);
        }
        UserDetailVO userDetailVO = UserDetailConvert.INSTANCE.useToDetail(userDO);
        userDetailVO.setInviteCode(inviteCode);
        return userDetailVO;
    }

    @Override
    public boolean changePassword(ChangePasswordRequest request) {
        RecoverPasswordDO recoverPasswordDO = recoverPasswordMapper.selectByCode(request.getVerificationCode());
        if (recoverPasswordDO == null || recoverPasswordDO.getStatus() != 0) {
            throw exception(ACTIVATION_CODE);
        }
        if (recoverPasswordDO.getRecoverDate().compareTo(LocalDateTime.now().minusMinutes(30)) < 0) {
            recoverPasswordDO.setStatus(2);
            recoverPasswordMapper.updateById(recoverPasswordDO);
            throw exception(OPERATE_TIME_OUT);
        }
        recoverPasswordDO.setStatus(1);
        recoverPasswordMapper.updateById(recoverPasswordDO);
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
