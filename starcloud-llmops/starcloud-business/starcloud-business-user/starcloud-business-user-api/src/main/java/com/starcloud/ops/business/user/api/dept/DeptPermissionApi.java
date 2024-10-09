package com.starcloud.ops.business.user.api.dept;

import java.util.Set;

public interface DeptPermissionApi {

    /**
     * 查询当前用户的权限点
     */
    Set<String> getUserPermission();

    /**
     * 判断当前用户是否有权限点
     */
    boolean hasPermission(String permission);
}
