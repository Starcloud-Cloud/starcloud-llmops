package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.security.core.service.SecurityFrameworkService;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-15
 */
@SuppressWarnings("all")
public class UserUtils {

    /**
     * 所有权限
     */
    public static final String ALL = "ALL";

    /**
     * 个人权限
     */
    public static final String SELF = "SELF";

    /**
     * 管理员角色
     */
    public static final String ADMIN_ROLE = "MOFAAI_ADMIN";

    /**
     * 后台运营角色
     */
    public static final String OPERATE_ROLE = "MOFAAI_DEV";

    /**
     * 部门权限
     */
    private static final PermissionApi PERMISSION_API = SpringUtil.getBean(PermissionApi.class);

    /**
     * 用户服务
     */
    private static final AdminUserService ADMIN_USER_SERVICE = SpringUtil.getBean(AdminUserService.class);

    /**
     * 安全框架服务
     */
    private static final SecurityFrameworkService SECURITY_FRAMEWORK_SERVICE = SpringUtil.getBean(SecurityFrameworkService.class);

    /**
     * 判断是否是管理员
     *
     * @return
     */
    public static Boolean isAdmin() {
        return SECURITY_FRAMEWORK_SERVICE.hasAnyRoles(ADMIN_ROLE, OPERATE_ROLE);
    }

    /**
     * 判断是否是不是管理员
     *
     * @return
     */
    public static Boolean isNotAdmin() {
        return !isAdmin();
    }

    /**
     * 标识用户
     *
     * @param userId  用户id
     * @param endUser 用户
     * @return 用户标识
     */
    public static String identify(String userId, String endUser) {
        // endUser 不为空，说明是游客
        if (StringUtils.isNotBlank(endUser)) {
            return "游客(" + endUser + ")";
        }
        return getUsername(userId);
    }

    /**
     * 游客标识
     *
     * @param endUser 游客
     * @return 游客标识
     */
    public static String visitorIdentify(String endUser) {
        return "游客(" + endUser + ")";
    }

    /**
     * 获取用户名称
     *
     * @param userId 用户id
     * @return 用户名称
     */
    public static String getUsername(String userId) {

        if (StringUtils.isBlank(userId) || StringUtils.equalsIgnoreCase(userId, "null")) {
            return "用户";
        }

        return getUsername(Long.valueOf(userId));
    }

    /**
     * 获取用户名称
     *
     * @param userId 用户id
     * @return 用户名称
     */
    public static String getUsername(Long userId) {

        if (Objects.isNull(userId)) {
            return "用户";
        }

        AdminUserDO user = ADMIN_USER_SERVICE.getUser(userId);
        if (Objects.nonNull(user)) {
            return user.getNickname();
        }

        return "用户";
    }

    /**
     * 获取部门ID
     *
     * @param userId 用户id
     * @return 部门ID
     */
    public static Long getDeptId(Long userId) {
        if (Objects.isNull(userId)) {
            return null;
        }
        AdminUserDO user = ADMIN_USER_SERVICE.getUser(userId);
        if (Objects.nonNull(user)) {
            return user.getDeptId();
        }
        return null;
    }

    /**
     * 根据用户 ID 集合，获得用户 Map
     *
     * @param userIds 用户 ID 集合
     * @return 用户 Map
     */
    public static Map<Long, AdminUserDO> getUserMapByIds(List<Long> userIds) {
        return ADMIN_USER_SERVICE.getUserMap(userIds);
    }

    /**
     * 根据用户 ID 集合，获得用户昵称 Map
     *
     * @param userIds 用户 ID 集合
     * @return 用户昵称 Map
     */
    public static Map<Long, String> getUserNicknameMapByIds(List<Long> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<AdminUserDO> userList = ADMIN_USER_SERVICE.getUserList(userIds);
        if (CollectionUtil.isEmpty(userList)) {
            return Collections.emptyMap();
        }
        return userList.stream().collect(Collectors.toMap(AdminUserDO::getId, AdminUserDO::getNickname));
    }

    /**
     * 根据用户 ID 集合，获得用户角色集合
     *
     * @param userIds 用户 ID 集合
     * @return 角色集合
     */
    public static Map<Long, List<String>> mapUserRoleCode(List<Long> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        return PERMISSION_API.mapRoleCodeListByUserIds(userIds);
    }

    /**
     * 根据用户 ID 集合，获得用户角色集合
     *
     * @param userIds 用户 ID 集合
     * @return 角色集合
     */
    public static Map<Long, List<String>> mapUserRoleName(List<Long> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        return PERMISSION_API.mapRoleNameListByUserIds(userIds);
    }

    /**
     * 获取部门数据权限
     *
     * @return 部门数据权限
     */
    public static String getDeptDataPermission() {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        DeptDataPermissionRespDTO deptDataPermission = PERMISSION_API.getDeptDataPermission(loginUserId);
        if (deptDataPermission.getAll()) {
            return ALL;
        }
        if (deptDataPermission.getSelf()) {
            return SELF;
        }
        // 部门 ID 集合
        if (CollectionUtil.isNotEmpty(deptDataPermission.getDeptIds())) {
            return StringUtils.join(deptDataPermission.getDeptIds(), ",");
        }

        return SELF;
    }

    public static Long getUserIdByUsername(String username) {
        AdminUserDO user = ADMIN_USER_SERVICE.getUserByUsername(username);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("用户不存在(" + username + ")");
        }
        return user.getId();
    }
}
