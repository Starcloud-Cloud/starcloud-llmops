package com.starcloud.ops.business.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.date.LocalDateTimeUtils;
import cn.iocoder.yudao.framework.common.util.monitor.TracerUtils;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.system.api.logger.dto.LoginLogCreateReqDTO;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthLoginRespVO;
import cn.iocoder.yudao.module.system.controller.admin.auth.vo.AuthPermissionInfoRespVO;
import cn.iocoder.yudao.module.system.convert.auth.AuthConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.dept.DeptDO;
import cn.iocoder.yudao.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.MenuDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.dal.mysql.dept.DeptMapper;
import cn.iocoder.yudao.module.system.dal.mysql.user.AdminUserMapper;
import cn.iocoder.yudao.module.system.enums.logger.LoginLogTypeEnum;
import cn.iocoder.yudao.module.system.enums.logger.LoginResultEnum;
import cn.iocoder.yudao.module.system.enums.oauth2.OAuth2ClientConstants;
import cn.iocoder.yudao.module.system.service.logger.LoginLogService;
import cn.iocoder.yudao.module.system.service.mail.MailSendServiceImpl;
import cn.iocoder.yudao.module.system.service.oauth2.OAuth2TokenService;
import cn.iocoder.yudao.module.system.service.permission.MenuService;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.product.api.spu.ProductSpuApi;
import com.starcloud.ops.business.product.api.spu.dto.ProductSpuRespDTO;
import com.starcloud.ops.business.promotion.api.coupon.CouponApi;
import com.starcloud.ops.business.promotion.api.coupon.dto.CouponRespDTO;
import com.starcloud.ops.business.trade.api.order.TradeOrderApi;
import com.starcloud.ops.business.user.api.SendUserMsgService;
import com.starcloud.ops.business.user.controller.admin.dept.vo.request.CreateUserDeptReqVO;
import com.starcloud.ops.business.user.controller.admin.level.vo.level.AdminUserLevelDetailRespVO;
import com.starcloud.ops.business.user.controller.admin.rights.vo.rights.AdminUserRightsCollectRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.AdminUserInfoRespVO;
import com.starcloud.ops.business.user.controller.admin.vo.UserDetailVO;
import com.starcloud.ops.business.user.convert.UserConvert;
import com.starcloud.ops.business.user.convert.UserDetailConvert;
import com.starcloud.ops.business.user.dal.dataobject.RecoverPasswordDO;
import com.starcloud.ops.business.user.dal.dataobject.RegisterUserDO;
import com.starcloud.ops.business.user.dal.mysql.RecoverPasswordMapper;
import com.starcloud.ops.business.user.dal.mysql.RegisterUserMapper;
import com.starcloud.ops.business.user.enums.dept.UserDeptRoleEnum;
import com.starcloud.ops.business.user.framework.user.config.NewUserProperties;
import com.starcloud.ops.business.user.pojo.dto.UserDTO;
import com.starcloud.ops.business.user.pojo.request.ChangePasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RecoverPasswordRequest;
import com.starcloud.ops.business.user.pojo.request.RegisterRequest;
import com.starcloud.ops.business.user.pojo.request.UserProfileUpdateRequest;
import com.starcloud.ops.business.user.service.SendSocialMsgService;
import com.starcloud.ops.business.user.service.dept.UserDeptService;
import com.starcloud.ops.business.user.service.invite.AdminUserInviteService;
import com.starcloud.ops.business.user.service.level.AdminUserLevelService;
import com.starcloud.ops.business.user.service.rights.AdminUserRightsService;
import com.starcloud.ops.business.user.service.tag.AdminUserTagService;
import com.starcloud.ops.business.user.service.user.StarUserService;
import com.starcloud.ops.business.user.service.user.handler.NewUserHandler;
import com.starcloud.ops.business.user.util.EncryptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
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


    @Resource
    private AdminUserInviteService adminUserInviteService;

    @Resource
    private AdminUserService userService;
    @Resource
    private LoginLogService loginLogService;

    @Resource
    private SendUserMsgService sendUserMsgService;

    @Resource
    private SendSocialMsgService sendSocialMsgService;

    @Resource
    private AdminUserLevelService adminUserLevelService;

    @Resource
    private AdminUserRightsService adminUserRightsService;

    @Resource
    private AdminUserTagService adminUserTagService;

    @Resource
    private TradeOrderApi tradeOrderApi;

    @Resource
    private UserDeptService userDeptService;

    @Resource
    private ProductSpuApi productSpuApi;

    @Resource
    private CouponApi couponApi;

    @Resource
    private List<NewUserHandler> newUserHandlers;

    @Resource
    private NewUserProperties newUserProperties;

    @Resource
    private PermissionService permissionService;

    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;


    @Override
    @Transactional(rollbackFor = Exception.class)
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
            try {
                Long inviteUserId = EncryptionUtils.decrypt(request.getInviteCode());
                registerUserDO.setInviteUserId(inviteUserId);
            } catch (Exception e) {
                log.warn("计算邀请码异常", e);
            }
        }
        String url = getOrigin();
        String activationUrl = url + "/registerResult?activation=" + activationCode;
        Map<String, Object> map = new HashMap<>();
        map.put("activationUrl", activationUrl);
        // 创建未激活用户
        UserDTO userDTO = UserDTO.builder().username(registerUserDO.getUsername())
                .email(registerUserDO.getEmail())
                .password(registerUserDO.getPassword())
                // 父部门id与租户id保持一致 ！！！
                .parentDeptId(TenantContextHolder.getTenantId())
                .tenantId(TenantContextHolder.getTenantId())
                .userStatus(CommonStatusEnum.DISABLE.getStatus()).build();
        Long userId = createNewUser(userDTO);
        registerUserDO.setUserId(userId);
        int insert = registerUserMapper.insert(registerUserDO);
        mailSendService.sendSingleMail(request.getEmail(), 1L, UserTypeEnum.ADMIN.getValue(), "register_temp", map);
        return insert > 0;
    }

    private String getOrigin() {
        HttpServletRequest servletRequest = ServletUtils.getRequest();
        return servletRequest.getHeader("Origin");
    }

    @Override
    public void addBenefits(Long currentUserId, Long inviteUserId) {
        // try {
        //     Long tenantId = TenantContextHolder.getTenantId();
        //     if (inviteUserId != null && inviteUserId > 0) {
        //
        //         // 增加邀请记录
        //         Long invitationId = adminUserInviteService.createInvitationRecords(inviteUserId, currentUserId);
        //         log.info("邀请记录添加成功，开始发送注册与邀请权益");
        //
        //         TenantUtils.execute(tenantId, () -> {
        //             AddRightsDTO newUserRightsDTO = new AddRightsDTO()
        //                     .setUserId(currentUserId)
        //                     .setMagicBean(AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getMagicBean())
        //                     .setMagicImage(AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getMagicImage())
        //                     .setMatrixBean(AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getMatrixBean())
        //                     .setTimeNums(1)
        //                     .setTimeRange(TimeRangeTypeEnum.MONTH.getType())
        //                     .setBizId(String.valueOf(currentUserId))
        //                     .setBizType(AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getType())
        //                     .setLevelId(null);
        //
        //             // 增加注册人权益
        //             adminUserRightsService.createRights(newUserRightsDTO);
        //
        //             AddRightsDTO inviteUserRightsDTO = new AddRightsDTO()
        //                     .setUserId(inviteUserId)
        //                     .setMagicBean(AdminUserRightsBizTypeEnum.USER_INVITE.getMagicBean())
        //                     .setMagicImage(AdminUserRightsBizTypeEnum.USER_INVITE.getMagicImage())
        //                     .setMatrixBean(AdminUserRightsBizTypeEnum.USER_INVITE.getMatrixBean())
        //                     .setTimeNums(1)
        //                     .setTimeRange(TimeRangeTypeEnum.MONTH.getType())
        //                     .setBizId(String.valueOf(invitationId))
        //                     .setBizType(AdminUserRightsBizTypeEnum.USER_INVITE.getType())
        //                     .setLevelId(null);
        //             // 增加邀请人权益
        //             adminUserRightsService.createRights(inviteUserRightsDTO);
        //         });
        //
        //         sendSocialMsgService.sendInviteMsg(inviteUserId);
        //
        //         // 获取当天的邀请记录
        //         List<AdminUserInviteDO> todayInvitations = adminUserInviteService.getTodayInvitations(inviteUserId);
        //         if (todayInvitations.size() % 3 == 0 && CollUtil.isNotEmpty(todayInvitations)) {
        //             log.info("用户【{}】已经邀请了【{}】人，开始赠送额外的权益", inviteUserId, todayInvitations.size());
        //             TenantUtils.execute(tenantId, () -> {
        //                 AddRightsDTO inviteUserRightsDTO = new AddRightsDTO()
        //                         .setUserId(inviteUserId)
        //                         .setMagicBean(AdminUserRightsBizTypeEnum.USER_INVITE_REPEAT.getMagicBean())
        //                         .setMagicImage(AdminUserRightsBizTypeEnum.USER_INVITE_REPEAT.getMagicImage())
        //                         .setMatrixBean(AdminUserRightsBizTypeEnum.USER_INVITE_REPEAT.getMatrixBean())
        //                         .setTimeNums(1)
        //                         .setTimeRange(TimeRangeTypeEnum.MONTH.getType())
        //                         .setBizId(String.valueOf(invitationId))
        //                         .setBizType(AdminUserRightsBizTypeEnum.USER_INVITE_REPEAT.getType())
        //                         .setLevelId(null);
        //                 adminUserRightsService.createRights(inviteUserRightsDTO);
        //             });
        //
        //             try {
        //                 sendUserMsgService.sendMsgToWx(inviteUserId, String.format(
        //                         "您已成功邀请了【%s】位朋友加入魔法AI大家庭，并成功解锁了一份独特的权益礼包【送3000字】" + "我们已经将这份珍贵的礼物送至您的账户中。" + "\n" + "\n" +
        //                                 "值得一提的是，每邀请三位朋友，您都将再次解锁一个全新的权益包，彰显您的独特地位。", todayInvitations.size()));
        //             } catch (Exception e) {
        //                 log.error("邀请达人公众号信息发送失败，currentUserId={},inviteUserId={}", currentUserId, inviteUserId, e);
        //             }
        //
        //         }
        //
        //     } else {
        //         // 普通注册权益
        //         TenantUtils.execute(tenantId, () -> {
        //
        //             AddRightsDTO newUserRightsDTO = new AddRightsDTO()
        //                     .setUserId(currentUserId)
        //                     .setMagicBean(AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getMagicBean())
        //                     .setMagicImage(AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getMagicImage())
        //                     .setMatrixBean(AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getMatrixBean())
        //                     .setTimeNums(1)
        //                     .setTimeRange(TimeRangeTypeEnum.MONTH.getType())
        //                     .setBizId(String.valueOf(currentUserId))
        //                     .setBizType(AdminUserRightsBizTypeEnum.INVITE_TO_REGISTER.getType())
        //                     .setLevelId(null);
        //
        //             adminUserRightsService.createRights(newUserRightsDTO);
        //         });
        //     }
        //
        // } catch (Exception e) {
        //     log.warn("新增权益失败，currentUserId={},inviteUserId={}", currentUserId, inviteUserId, e);
        // }
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
        // 2.0 权益统一处理
        Long finalInviteUserid = inviteUserid;
        try {
            newUserHandlers.forEach(handler -> handler.afterUserRegister(userService.getUser(currentUserId), finalInviteUserid == null ? null : userService.getUser(finalInviteUserid)));
        } catch (RuntimeException e) {
            log.error("新用户权益发放失败，失败原因{}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activation(String activationCode) {
        RegisterUserDO registerUserDO = registerUserMapper.selectByActivationCode(activationCode);
        if (registerUserDO == null) {
            throw exception(ACTIVATION_CODE_ERROR);
        }
        if (registerUserDO.getRegisterDate().compareTo(LocalDateTime.now().minusMinutes(60)) < 0) {
            registerUserDO.setStatus(2);
            registerUserMapper.updateById(registerUserDO);
            throw exception(OPERATE_TIME_OUT);
        }
        TenantContextHolder.setTenantId(registerUserDO.getTenantId());
        TenantContextHolder.setIgnore(false);
        AdminUserDO userDO = new AdminUserDO().setId(registerUserDO.getUserId()).setStatus(CommonStatusEnum.ENABLE.getStatus());
        int i = adminUserMapper.updateById(userDO);
        if (i <= 0) {
            throw exception(ACTIVATION_USER_ERROR);
        }

        try {
            newUserHandlers.forEach(handler -> handler.afterUserRegister(userService.getUser(registerUserDO.getUserId()), registerUserDO.getInviteUserId() == null ? null : userService.getUser(registerUserDO.getInviteUserId())));
        } catch (RuntimeException e) {
            log.error("新用户权益发放失败，失败原因{}", e.getMessage(), e);
        }

        // addBenefits(registerUserDO.getUserId(), registerUserDO.getInviteUserId());
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
        deptDO.setTenantId(userDTO.getTenantId());
        deptMapper.insert(deptDO);

        Long deptId = deptDO.getId();
        AdminUserDO userDO = new AdminUserDO();
        userDO.setDeptId(deptId);
        userDO.setUsername(userDTO.getUsername());
        userDO.setEmail(userDTO.getEmail());
        userDO.setStatus(userDTO.getUserStatus());
        userDO.setNickname(userDTO.getUsername());
        userDO.setPassword(userDTO.getPassword());
        userDO.setTenantId(userDTO.getTenantId());
        userDO.setMobile(userDTO.getMobile());
        adminUserMapper.insert(userDO);

        CreateUserDeptReqVO createUserDeptReqVO = CreateUserDeptReqVO.builder().deptId(deptId)
                .userId(userDO.getId())
                .inviteUser(userDO.getId())
                .deptRole(UserDeptRoleEnum.SUPER_ADMIN.getRoleCode()).build();
        userDeptService.create(createUserDeptReqVO);

        deptDO.setLeaderUserId(userDO.getId());
        deptMapper.updateById(deptDO);

        // TenantUtils.execute(userDTO.getTenantId(), () -> {
        //     adminUserLevelService.createInitLevelRecord(userDO.getId());
        //     adminUserTagService.addNewUserTag(userDO.getId());
        // });

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

    /**
     * 用户是否是新用户
     *
     * @param userId 用户 ID
     * @return 是否是新用户
     */
    @Override
    public Boolean isNewUser(Long userId) {

        AdminUserDO userDO = adminUserMapper.selectById(userId);

        if (!userDO.getCreateTime().isAfter(
                LocalDateTimeUtils.minusTime(newUserProperties.getRegisterTime()))) {
            return false;
        }

        if (newUserProperties.getValidOrder()) {
            return tradeOrderApi.getSuccessOrderCount(userId) == 0;
        }

        return true;
    }

    /**
     * @param userId 用户ID
     * @return AuthPermissionInfoRespVO
     */
    @Override
    public AuthPermissionInfoRespVO getPermissionInfo(Long userId) {
        // 1.1 获得用户信息
        AdminUserDO user = userService.getUser(userId);
        if (user == null) {
            return null;
        }
        Long permissionUser;
        // 获取当前用户的团队管理者
        Long deptRightsUserId = getDeptRightsUserId(userId);
        if (!userId.equals(deptRightsUserId)) {
            permissionUser = deptRightsUserId;
            log.info("当前用户{}存在团队，且不属于团队管理者，开始切换显示团队管理者{}菜单", userId, deptRightsUserId);
        } else {
            permissionUser = userId;
        }
        // 1.2 获得角色列表
        Set<Long> roleIds = permissionService.getUserRoleIdListByUserId(permissionUser);
        if (CollUtil.isEmpty(roleIds)) {
            return AuthConvert.INSTANCE.convert(user, Collections.emptyList(), Collections.emptyList());
        }
        List<RoleDO> roles = roleService.getRoleList(roleIds);
        roles.removeIf(role -> !CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())); // 移除禁用的角色

        // 1.3 获得菜单列表
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(convertSet(roles, RoleDO::getId));
        List<MenuDO> menuList = menuService.getMenuList(menuIds);
        menuList.removeIf(menu -> !CommonStatusEnum.ENABLE.getStatus().equals(menu.getStatus())); // 移除禁用的菜单
        return AuthConvert.INSTANCE.convert(user, roles, menuList);
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
        map.put("username", userDO.getUsername());

        RecoverPasswordDO recoverPasswordDO = new RecoverPasswordDO();
        recoverPasswordDO.setUserId(userDO.getId());
        recoverPasswordDO.setRecoverCode(recoverCode);
        recoverPasswordDO.setRecoverDate(LocalDateTime.now());
        recoverPasswordDO.setRecoverIp(ServletUtils.getClientIP());
        recoverPasswordDO.setEmail(request.getEmail());
        recoverPasswordDO.setStatus(0);
        recoverPasswordMapper.insert(recoverPasswordDO);
        mailSendService.sendSingleMail(request.getEmail(), userDO.getId(), UserTypeEnum.ADMIN.getValue(), "recover_temp", map);
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
        List<AdminUserLevelDetailRespVO> levelList = adminUserLevelService.getLevelList(userId);
        // 获取团队所属者用户等级
        List<AdminUserLevelDetailRespVO> groupLevelList = adminUserLevelService.getGroupLevelList(userId);
        // 获取用户权益
        List<AdminUserRightsCollectRespVO> rightsCollect = adminUserRightsService.getRightsCollect(userId);
        // 获取团队权益
        List<AdminUserRightsCollectRespVO> teamRights = adminUserRightsService.getGroupRightsCollect(userId);


        AdminUserInfoRespVO userDetailVO = UserDetailConvert.INSTANCE.useToDetail02(userDO, levelList, rightsCollect, teamRights);
        userDetailVO.setInviteCode(inviteCode);
        userDetailVO.setInviteUrl(String.format("%s/login?inviteCode=%s", getOrigin(), inviteCode));
        userDetailVO.setIsNewUser(isNewUser(userId));
        userDetailVO.setRegisterTime(userDO.getCreateTime());
        userDetailVO.setEndTime(userDO.getCreateTime().plusDays(3));
        userDetailVO.setIsInviteUser(false);

        List<ProductSpuRespDTO> spuRespDTOS = productSpuApi.getSpuListByKeywordOrCategoryId(userId, "invite_try", null);

        if (CollUtil.isNotEmpty(spuRespDTOS)) {
            spuRespDTOS.forEach(spu -> {
                List<Long> ids1 = JSON.parseArray(JSON.toJSONString(spu.getLimitCouponTemplateIds()), Long.class);
                ids1.forEach(coupon ->
                {
                    List<CouponRespDTO> couponRespDTOList = couponApi.getMatchCouponByTemplateId(userId, coupon);
                    if (CollUtil.isNotEmpty(couponRespDTOList)) {
                        userDetailVO.setInviteEndTime(couponRespDTOList.get(0).getValidEndTime());
                    }
                });

            });
            userDetailVO.setIsInviteUser(true);
        }
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
        if (registerUserDO != null && registerUserDO.getRegisterDate().compareTo(LocalDateTime.now().minusMinutes(60)) > 0) {
            throw exception(USER_USERNAME_EXISTS);
        }
        registerUserDO = registerUserMapper.selectByEmail(email);
        if (registerUserDO != null && registerUserDO.getRegisterDate().compareTo(LocalDateTime.now().minusMinutes(60)) > 0) {
            throw exception(USER_EMAIL_EXISTS);
        }
    }


        /*
      获取应该创作权益的用户   返回部门超级管理员id
      1，获取当前用户的部门
      2，判断是否是部门管理员
      1）是部门管理员，返回
      2）不是部门管理员，优先获取部门管理员。判断管理员有无剩余点数
      3，返回有剩余点的用户ID（管理员或当前用户）
     */

    /**
     * 这里关闭数据权限，主要是后面的 SQL查询会带上 kstry 线程中的其他正常用户的上下文，导致跟 powerjob 执行应用时候导致用户上下文冲突
     * 所以这里直接 关闭数据权限，这样下面的 关于权益的扣点 已经不需要用户上下文了，单ruiyi 本地比如SQL update会继续获取，所以后续的方法最好直接指定字段创作DB。
     */
    protected Long getDeptRightsUserId(Long currentUserId) {
        return userDeptService.selectSuperAdminId(currentUserId).getUserId();
    }

}


