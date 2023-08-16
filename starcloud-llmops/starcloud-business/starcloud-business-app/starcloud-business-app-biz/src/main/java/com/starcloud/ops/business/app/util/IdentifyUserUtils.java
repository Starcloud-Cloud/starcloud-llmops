package com.starcloud.ops.business.app.util;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.iocoder.yudao.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-15
 */
@Component
public class IdentifyUserUtils {

    @Resource
    private PermissionApi permissionApi;

    @Resource
    private AdminUserService adminUserService;

    /**
     * 标识用户
     *
     * @param userId  用户id
     * @param endUser 用户
     * @return 用户标识
     */
    public String identifyUser(String userId, String endUser) {

        // endUser 不为空，说明是游客
        if (StringUtils.isNotBlank(endUser)) {
            return "游客";
        }

        // endUser 为空，userId 不为空，说明是登录用户
        if (StringUtils.isNotBlank(userId)) {
            Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
            DeptDataPermissionRespDTO deptDataPermission = permissionApi.getDeptDataPermission(loginUserId);
            if (deptDataPermission.getAll()) {
                AdminUserDO user = adminUserService.getUser(Long.valueOf(userId));
                if (Objects.nonNull(user)) {
                    return user.getNickname();
                }
            }
        }

        return "用户";
    }

}
