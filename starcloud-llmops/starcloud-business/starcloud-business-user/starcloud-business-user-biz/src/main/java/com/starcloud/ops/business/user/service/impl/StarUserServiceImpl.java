package com.starcloud.ops.business.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.monitor.TracerUtils;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.system.api.logger.dto.LoginLogCreateReqDTO;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import cn.iocoder.yudao.module.system.convert.auth.AuthConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.dept.DeptMapper;
import cn.iocoder.yudao.module.system.dal.mysql.permission.RoleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.permission.UserRoleMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.enums.logger.LoginLogTypeEnum;
import cn.iocoder.yudao.module.system.enums.logger.LoginResultEnum;
import cn.iocoder.yudao.module.system.enums.oauth2.OAuth2ClientConstants;
import cn.iocoder.yudao.module.system.service.logger.LoginLogService;
import cn.iocoder.yudao.module.system.service.mail.MailSendServiceImpl;
import cn.iocoder.yudao.module.system.service.oauth2.OAuth2TokenService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.starcloud.ops.business.user.api.SendUserMsgService;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelDetailRespVO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.AdminUserRightsCollectRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.AdminUserInfoRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.UserDetailVO;
import com.starcloud.ops.business.user.convert.UserConvert;
import com.starcloud.ops.business.user.convert.UserDetailConvert;
import com.starcloud.ops.business.user.dal.dataObject.InvitationRecordsDO;
import com.starcloud.ops.business.user.dal.dataObject.RecoverPasswordDO;
import com.starcloud.ops.business.user.dal.dataobject.RegisterUserDO;
import com.starcloud.ops.business.user.dal.dataobject.level.AdminUserLevelDO;
import com.starcloud.ops.business.user.dal.mysql.RecoverPasswordMapper;
import com.starcloud.ops.business.user.dal.mysql.RegisterUserMapper;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsBizTypeEnum;
import com.starcloud.ops.business.user.pojo.dto.UserDTO;
import com.starcloud.ops.business.user.pojo.request.ChangePasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RecoverPasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RegisterRequest;
import com.starcloud.ops.business.user.pojo.request.UserProfileUpdateRequest;
import com.starcloud.ops.business.user.service.InvitationRecordsService;
import com.starcloud.ops.business.user.service.SendSocialMsgService;
import com.starcloud.ops.business.user.service.StarUserService;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import com.starcloud.ops.business.user.util.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Resource
    private OAuth2TokenService oauth2TokenService;

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

//    @Autowired
//    private PermissionProducer permissionProducer;

//    @Autowired
//    private UserBenefitsService benefitsService;

    @Resource
    private InvitationRecordsService invitationRecordsService;

    @Resource
    private AdminUserService userService;
    @Resource
    private LoginLogService loginLogService;

    @Resource
    private SendUserMsgService sendUserMsgService;


    @Autowired
    private RoleMapper roleMapper;
    @Resource
    private SendSocialMsgService sendSocialMsgService;


    @Resource
    private AdminUserLevelService adminUserLevelService;

    @Resource
    private AdminUserRightsService adminUserRightsService;


    @Value("${starcloud-llm.role.code:mofaai_free}")
    private String roleCode;

    @Value("${starcloud-llm.tenant.id:2}")
    private Long tenantId;


    @Override
    @Transactional(rollbackFor = Exception.class)
//    @Cacheable(value = RedisKeyConstants.MENU_ROLE_ID_LIST, key = "#menuId")
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
//            String userName = null;
            try {
//                userName = EncryptionUtils.decryptString(request.getInviteCode());
                Long inviteUserId = EncryptionUtils.decrypt(request.getInviteCode());
                registerUserDO.setInviteUserId(inviteUserId);
//                AdminUserDO userDO = adminUserMapper.selectByUsername(userName);
//                if (userDO != null) {
//                    registerUserDO.setInviteUserId(userDO.getId());
//                }
            } catch (Exception e) {
                log.warn("计算邀请码异常", e);
            }
        }
        String url = getUrl();
        String activationUrl = url + "/admin-api/llm/auth/activation/" + activationCode + "?redirectUri=" + getOrigin() + "/login";
        Map<String, Object> map = new HashMap<>();
        map.put("activationUrl", activationUrl);
        // 创建未激活用户
        UserDTO userDTO = UserDTO.builder().username(registerUserDO.getUsername())
                .email(registerUserDO.getEmail())
                .password(registerUserDO.getPassword())
                .parentDeptId(2L)
                .userStatus(CommonStatusEnum.DISABLE.getStatus()).build();
        Long userId = createNewUser(userDTO);
        registerUserDO.setUserId(userId);
        mailSendService.sendSingleMail(request.getEmail(), 1L, UserTypeEnum.ADMIN.getValue(), "register_temp", map);
        int insert = registerUserMapper.insert(registerUserDO);
//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            @Override
//            public void afterCommit() {
//                permissionProducer.sendUserRoleRefreshMessage();
//            }
//
//        });
        return insert > 0;
    }

    private String getOrigin() {
        HttpServletRequest servletRequest = ServletUtils.getRequest();
        return servletRequest.getHeader("Origin");
    }

    @Override
    public void addBenefits(Long currentUserId, Long inviteUserId) {
        try {
            if (inviteUserId != null && inviteUserId > 0) {

                // 增加邀请记录
                Long invitationId = invitationRecordsService.createInvitationRecords(inviteUserId, currentUserId);
                log.info("邀请记录添加成功，开始发送注册与邀请权益");

                TenantUtils.execute(tenantId, () -> {
                    adminUserRightsService.createRights(currentUserId, AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getMagicBean(), AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getMagicImage(), null, null, AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER, String.valueOf(currentUserId));
                    adminUserRightsService.createRights(inviteUserId, AdminUserRightsBizTypeEnum.USER_INVITE.getMagicBean(), AdminUserRightsBizTypeEnum.USER_INVITE.getMagicImage(), null, null, AdminUserRightsBizTypeEnum.USER_INVITE, String.valueOf(invitationId));
                });

                sendSocialMsgService.sendInviteMsg(inviteUserId);

                // 获取当天的邀请记录
                List<InvitationRecordsDO> todayInvitations = invitationRecordsService.getTodayInvitations(inviteUserId);
                if (todayInvitations.size() % 3 == 0 && CollUtil.isNotEmpty(todayInvitations)) {
                    log.info("用户【{}】已经邀请了【{}】人，开始赠送额外的权益", inviteUserId, todayInvitations.size());
                    TenantUtils.execute(tenantId, () -> {
                        adminUserRightsService.createRights(inviteUserId, AdminUserRightsBizTypeEnum.USER_INVITE_REPEAT.getMagicBean(), AdminUserRightsBizTypeEnum.USER_INVITE_REPEAT.getMagicImage(), null, null, AdminUserRightsBizTypeEnum.USER_INVITE_REPEAT, String.valueOf(invitationId));
                    });
                    sendUserMsgService.sendMsgToWx(inviteUserId, String.format(
                            "您已成功邀请了【%s】位朋友加入魔法AI大家庭，并成功解锁了一份独特的权益礼包【送3000字】" + "我们已经将这份珍贵的礼物送至您的账户中。" + "\n" + "\n" +
                                    "值得一提的是，每邀请三位朋友，您都将再次解锁一个全新的权益包，彰显您的独特地位。", todayInvitations.size()));
                }

            } else {
                // 普通注册权益
                TenantUtils.execute(tenantId, () -> {
                    adminUserRightsService.createRights(currentUserId, AdminUserRightsBizTypeEnum.REGISTER.getMagicBean(), AdminUserRightsBizTypeEnum.REGISTER.getMagicImage(), null, null, AdminUserRightsBizTypeEnum.REGISTER, String.valueOf(currentUserId));
                });
            }

        } catch (Exception e) {
            log.warn("新增权益失败，currentUserId={},inviteUserId={}", currentUserId, inviteUserId, e);
        }
    }

    @Override
    public void addInviteBenefits(Long currentUserId, String inviteCode) {
        Long inviteUserid = null;
        try {
            if (StringUtils.isNotBlank(inviteCode)) {
                inviteUserid = EncryptionUtils.decrypt(inviteCode);
            }
        } catch (Exception e) {
            log.warn("解析邀请用户失败，currentUserId={},inviteCode={}", currentUserId, inviteCode, e);
        }
        addBenefits(currentUserId, inviteUserid);
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

        AdminUserDO userDO = new AdminUserDO().setId(registerUserDO.getUserId()).setStatus(CommonStatusEnum.ENABLE.getStatus());
        int i = adminUserMapper.updateById(userDO);
        if (i <= 0) {
            throw exception(ACTIVATION_USER_ERROR);
        }
        TenantContextHolder.setTenantId(tenantId);
        TenantContextHolder.setIgnore(false);
        addBenefits(registerUserDO.getUserId(), registerUserDO.getInviteUserId());
        TenantContextHolder.setIgnore(true);
        registerUserDO.setStatus(1);
        registerUserMapper.updateById(registerUserDO);
        return true;
    }

    @Override
    public Long createNewUser(UserDTO userDTO) {
        DeptDO deptDO = new DeptDO();
        deptDO.setParentId(userDTO.getParentDeptId());
        deptDO.setName(userDTO.getUsername() + "_space");
        deptDO.setEmail(userDTO.getEmail());
        deptDO.setStatus(0);
        deptDO.setTenantId(tenantId);
        deptMapper.insert(deptDO);

        Long deptId = deptDO.getId();
        AdminUserDO userDO = new AdminUserDO();
        userDO.setDeptId(deptId);
        userDO.setUsername(userDTO.getUsername());
        userDO.setEmail(userDTO.getEmail());
        userDO.setStatus(userDTO.getUserStatus());
        userDO.setNickname(userDTO.getUsername());
        userDO.setPassword(userDTO.getPassword());
        userDO.setTenantId(tenantId);
        userDO.setMobile(userDTO.getMobile());
        adminUserMapper.insert(userDO);

//        RoleDO roleDO = roleMapper.selectByCode(roleCode, tenantId);
//        if (roleDO == null) {
//            throw exception(ROLE_NOT_EXIST);
//        }

        // FIXME: 2023/12/19  设置用户等级 而不是设置设置用户角色
        TenantUtils.execute(tenantId, () -> {
            adminUserLevelService.createInitLevelRecord(userDO.getId());
        });

//        UserRoleDO userRoleDO = new UserRoleDO();
//        userRoleDO.setRoleId(roleDO.getId());
//        userRoleDO.setUserId(userDO.getId());
//        userRoleDO.setCreator(userDO.getUsername());
//        userRoleDO.setUpdater(userDO.getUpdater());
////        userRoleDO.setTenantId(userDO.getTenantId());
//        userRoleMapper.insert(userRoleDO);
        return userDO.getId();
    }

    @Override
    public AuthLoginRespVO createTokenAfterLoginSuccess(Long userId, String username, LoginLogTypeEnum logType) {
        // 插入登陆日志
        createLoginLog(userId, username, logType, LoginResultEnum.SUCCESS);
        // 创建访问令牌
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(userId, UserTypeEnum.ADMIN.getValue(),
                OAuth2ClientConstants.CLIENT_ID_DEFAULT, null);
        // 构建返回结果
        return AuthConvert.INSTANCE.convert(accessTokenDO);
    }

    private void createLoginLog(Long userId, String username,
                                LoginLogTypeEnum logTypeEnum, LoginResultEnum loginResult) {
        // 插入登录日志
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setLogType(logTypeEnum.getType());
        reqDTO.setUsername(username);
        reqDTO.setUserId(userId);
        reqDTO.setUserType(UserTypeEnum.ADMIN.getValue());
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setResult(loginResult.getResult());
        reqDTO.setUserIp(ServletUtils.getClientIP());
        loginLogService.createLoginLog(reqDTO);
        // 更新最后登录时间
        if (userId != null && Objects.equals(LoginResultEnum.SUCCESS.getResult(), loginResult.getResult())) {
            userService.updateUserLogin(userId, ServletUtils.getClientIP());
        }
    }

    @Override
    public Boolean updateUserProfile(UserProfileUpdateRequest request) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            throw exception(AUTH_TOKEN_EXPIRED);
        }
        int length = request.getUsername().length();
        if (4 > length || length > 16) {
            throw exception(USERNAME_SIZE_ERROR);
        }

        DataPermissionUtils.executeIgnore(() -> {
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
            inviteCode = EncryptionUtils.encrypt(userDO.getId()).toString();
        } catch (Exception e) {
            throw exception(ENCRYPTION_ERROR);
        }
        UserDetailVO userDetailVO = UserDetailConvert.INSTANCE.useToDetail(userDO);
        userDetailVO.setInviteCode(inviteCode);
        String inviteUrl = getOrigin() + "/login?inviteCode=" + inviteCode;
        userDetailVO.setInviteUrl(inviteUrl);
        return userDetailVO;
    }

    /**
     * 获取用户详情 包含用户等级 用户权益
     *
     * @param userId
     * @return
     */
    @Override
    public AdminUserInfoRespVO userDetail(Long userId) {
        AdminUserDO userDO = adminUserMapper.selectById(userId);
        String inviteCode = null;
        try {
            inviteCode = EncryptionUtils.encrypt(userDO.getId()).toString();
        } catch (Exception e) {
            throw exception(ENCRYPTION_ERROR);
        }

        // 获取用户等级
        List<AdminUserLevelDetailRespVO>  levelList = adminUserLevelService.getLevelList(userId);
        // 获取用户权益
        List<AdminUserRightsCollectRespVO> rightsCollect = adminUserRightsService.getRightsCollect(userId);

        AdminUserInfoRespVO userDetailVO = UserDetailConvert.INSTANCE.useToDetail02(userDO, levelList, rightsCollect);
        userDetailVO.setInviteCode(inviteCode);
        userDetailVO.setInviteUrl(String.format("%s/login?inviteCode=%s", getOrigin(), inviteCode));
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
