package cn.iocoder.yudao.module.system.api.permission;

import cn.iocoder.yudao.module.system.api.permission.dto.DeptDataPermissionRespDTO;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 权限 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class PermissionApiImpl implements PermissionApi {

    @Resource
    private PermissionService permissionService;

    @Override
    public Set<Long> getUserRoleIdListByRoleIds(Collection<Long> roleIds) {
        return permissionService.getUserRoleIdListByRoleId(roleIds);
    }

    @Override
    public boolean hasAnyPermissions(Long userId, String... permissions) {
        return permissionService.hasAnyPermissions(userId, permissions);
    }

    @Override
    public boolean hasAnyRoles(Long userId, String... roles) {
        return permissionService.hasAnyRoles(userId, roles);
    }

    @Override
    public DeptDataPermissionRespDTO getDeptDataPermission(Long userId) {
        return permissionService.getDeptDataPermission(userId);
    }

    /**
     * 获得用户拥有的角色编号集合
     *
     * @param userIds 用户编号集合
     * @return 角色编号集合
     */
    @Override
    public Map<Long, List<String>> mapRoleCodeListByUserIds(Collection<Long> userIds) {
        return permissionService.mapRoleCodeListByUserIds(userIds);
    }

    /**
     * 获得用户拥有的角色名称集合
     *
     * @param userIds 用户编号集合
     * @return 角色名称集合
     */
    @Override
    public Map<Long, List<String>> mapRoleNameListByUserIds(Collection<Long> userIds) {
        return permissionService.mapRoleNameListByUserIds(userIds);
    }

    /**
     * 【管理员】新增用户角色
     *
     * @param userId   用户编号集合
     * @param roleCode 角色编码
     */
    @Override
    public void appendRole(Long userId, String roleCode) {
        permissionService.addUserRole(userId, roleCode);
    }

}
