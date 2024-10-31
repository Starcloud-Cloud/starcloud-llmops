package cn.iocoder.yudao.module.system.api.permission;

import cn.iocoder.yudao.module.system.dal.dataobject.permission.RoleDO;
import cn.iocoder.yudao.module.system.service.permission.PermissionService;
import cn.iocoder.yudao.module.system.service.permission.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class RoleApiImpl implements RoleApi {

    @Resource
    private PermissionService permissionService;

    @Resource
    private RoleService roleService;

    @Override
    public void validRoleList(Collection<Long> ids) {
        roleService.validateRoleList(ids);
    }

    /**
     * 获取当前用户所有角色数据
     *
     * @param userId 角色编号数组
     */
    @Override
    public List<String> getRoleNameList(Long userId) {


        // 调用
        Set<Long> result = permissionService.getUserRoleIdListByUserId(userId);
        List<RoleDO> roles = roleService.getRoleList(result);

        return roles.stream().map(RoleDO::getName).collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleCodeList(Long userId) {
        Set<Long> result = permissionService.getUserRoleIdListByUserId(userId);
        List<RoleDO> roles = roleService.getRoleList(result);

        return roles.stream().map(RoleDO::getCode).collect(Collectors.toList());
    }
}
