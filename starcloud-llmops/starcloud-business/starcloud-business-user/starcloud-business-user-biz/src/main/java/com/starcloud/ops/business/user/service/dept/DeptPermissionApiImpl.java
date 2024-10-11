package com.starcloud.ops.business.user.service.dept;

import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.user.api.dept.DeptPermissionApi;
import com.starcloud.ops.business.user.enums.dept.DeptPermissionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    public void checkPermission(DeptPermissionEnum permission, Long creator) {
        if (hasPermission(permission.getPermission(), creator)) {
            return;
        }
        throw exception(NO_PERMISSION, permission.getDesc());
    }

    @Override
    public void checkPermission(DeptPermissionEnum permission, List<Long> creatorList) {
        //todo
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
