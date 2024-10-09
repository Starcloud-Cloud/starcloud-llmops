package com.starcloud.ops.business.user.service.dept;

import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import com.starcloud.ops.business.user.api.dept.DeptPermissionApi;
import com.starcloud.ops.business.user.dal.dataObject.dept.UserDeptDO;
import com.starcloud.ops.business.user.enums.dept.UserDeptRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class DeptPermissionApiImpl implements DeptPermissionApi {

    @Resource
    private AdminUserApi adminUserApi;

    @Resource
    private UserDeptService userDeptService;

    @Override
    public Set<String> getUserPermission() {
        Long userId = WebFrameworkUtils.getLoginUserId();
        AdminUserRespDTO user = adminUserApi.getUser(userId);
        UserDeptDO userDeptDO = userDeptService.selectByDeptAndUser(user.getDeptId(), userId);
        return UserDeptRoleEnum.getByRoleCode(userDeptDO.getDeptRole()).getPermissions();
    }

    @Override
    public boolean hasPermission(String permission, Long creator) {
        Long userId = WebFrameworkUtils.getLoginUserId();
        // 创建者具有所有权限
        if (Objects.equals(userId, creator)) {
            return true;
        }

        Set<String> userPermission = getUserPermission();
        return userPermission.contains(permission);
    }
}
