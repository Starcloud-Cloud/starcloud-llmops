package com.starcloud.ops.business.app.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-15
 */
public class DataPermissionUtils {

    /**
     * 所有权限
     */
    public static final String ALL = "ALL";

    /**
     * 个人权限
     */
    public static final String SELF = "SELF";

    /**
     * 部门权限
     */
    private static final PermissionApi PERMISSION_API = SpringUtil.getBean(PermissionApi.class);

    /**
     * 用户服务
     */
    private static final AdminUserService ADMIN_USER_SERVICE = SpringUtil.getBean(AdminUserService.class);

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
            return "游客";
        }
        return getUsername(userId);
    }

    /**
     * 获取用户名称
     *
     * @param userId 用户id
     * @return 用户名称
     */
    public static String getUsername(String userId) {

        AdminUserDO user = ADMIN_USER_SERVICE.getUser(Long.valueOf(userId));
        if (Objects.nonNull(user)) {
            return user.getNickname();
        }

        return "用户";
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

}
