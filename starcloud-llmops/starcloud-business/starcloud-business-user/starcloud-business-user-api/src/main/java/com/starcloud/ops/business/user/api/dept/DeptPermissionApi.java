package com.starcloud.ops.business.user.api.dept;

import com.starcloud.ops.business.user.enums.dept.DeptPermissionEnum;

import java.util.Set;

public interface DeptPermissionApi {

    /**
     * 查询当前用户的权限点
     */
    Set<String> getUserPermission();

    /**
     * 校验权限
     * @param permission
     * @param creator
     */
    void checkPermission(DeptPermissionEnum permission, Long creator);

    /**
     * 判断当前用户是否有权限点
     */
    boolean hasPermission(String permission, Long creator);
}
