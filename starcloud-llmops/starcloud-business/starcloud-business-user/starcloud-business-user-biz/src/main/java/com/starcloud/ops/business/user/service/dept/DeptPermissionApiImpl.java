package com.starcloud.ops.business.user.service.dept;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.user.api.dept.DeptPermissionApi;
import com.starcloud.ops.business.user.enums.dept.DeptPermissionEnum;
import com.starcloud.ops.business.user.enums.dept.PartEnum;
import com.starcloud.ops.business.user.enums.dept.UserDeptRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.NO_PERMISSION;

@Slf4j
@Component
public class DeptPermissionApiImpl implements DeptPermissionApi {

    @Resource
    private UserDeptService userDeptService;

    @Override
    public Set<String> getUserPermission() {
        return userDeptService.getUserPermission();
    }

    @Override
    public Set<String> getUserPermission(Long creator) {
        Long userId = WebFrameworkUtils.getLoginUserId();
        if (Objects.equals(userId, creator)) {
            return UserDeptRoleEnum.SUPER_ADMIN.getPermissions();
        }
        return userDeptService.getUserPermission();
    }

    @Override
    public Map<String, Boolean> getUserMenu(Long creator, PartEnum partEnum) {
        Set<String> permissions = getUserPermission(creator);
        List<String> partPermissions = DeptPermissionEnum.getPermission(partEnum);
        Map<String, Boolean> result = new HashMap<>(partPermissions.size());
        for (String partPermission : partPermissions) {
            if (permissions.contains(partPermission)) {
                result.put(partPermission, true);
            } else {
                result.put(partPermission, false);
            }
        }
        return result;
    }

    @Override

    public void checkPermission(DeptPermissionEnum permission, Long creator) {
        if (hasPermission(permission.getPermission(), creator)) {
            return;
        }
        throw exception(NO_PERMISSION, permission.getDesc());
    }

    @Override
    public void checkPermission(DeptPermissionEnum permission, List<Long> creatorList) {
        Long userId = WebFrameworkUtils.getLoginUserId();
        List<Long> creators = creatorList.stream().distinct().filter(id -> !Objects.equals(id, userId)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(creators)) {
            return;
        }
        if (hasPermission(permission.getDesc(), null)) {
            return;
        }
        throw exception(NO_PERMISSION, permission.getDesc());
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
