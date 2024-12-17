package com.starcloud.ops.business.user.api.dept;

import com.starcloud.ops.business.user.enums.dept.DeptPermissionEnum;
import com.starcloud.ops.business.user.enums.dept.PartEnum;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DeptPermissionApi {

    /**
     * 查询当前用户的权限点
     */
    Set<String> getUserPermission();

    /**
     * 筛选组件权限点
     */
    Map<String, Boolean> getUserMenu(Long creator, PartEnum partEnum);

    /**
     * 获取页面菜单
     */
    Set<String> getUserPermission(Long creator);

    /**
     * 校验权限
     *
     * @param permission
     * @param creator
     */
    void checkPermission(DeptPermissionEnum permission, Long creator);

    /**
     * 校验权限
     */
    void checkPermission(DeptPermissionEnum permission, List<Long> creatorList);

    /**
     * 判断当前用户是否有权限点
     */
    boolean hasPermission(String permission, Long creator);

    /**
     * 校验当前用户是不是若依超级管理员，有没有编辑权限
     * <p>
     * super_admin 只能修改自己所在团队的数据
     *
     * @param deptIds 修改数据的deptId 若有多条数据绑定传入所有数据的deptId
     */
    void adminEditPermission(Long... deptIds);
}
