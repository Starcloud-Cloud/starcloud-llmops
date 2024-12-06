package cn.iocoder.yudao.module.system.api.user;

import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.datapermission.core.util.DataPermissionUtils;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.system.api.permission.RoleApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.system.convert.user.UserConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO;
import cn.iocoder.yudao.module.system.service.dept.DeptService;
import cn.iocoder.yudao.module.system.service.user.AdminUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Admin 用户 API 实现类
 *
 * @author 芋道源码
 */
@Service
public class AdminUserApiImpl implements AdminUserApi {

    @Resource
    private AdminUserService userService;

    @Resource
    private RoleApi roleApi;

    @Resource
    private DeptService deptService;

    @Override
    public AdminUserRespDTO getUser(Long id) {
        AdminUserDO user = userService.getUser(id);
        return UserConvert.INSTANCE.convert4(user);
    }

    @Override
    @TenantIgnore
    public Long getTenantId(Long id) {
        AtomicReference<Long> tenantId = new AtomicReference<>();
        DataPermissionUtils.executeIgnore(() -> {
            tenantId.set(userService.getUser(id).getTenantId());
        });
        return tenantId.get();
    }

    @Override
    public List<AdminUserRespDTO> getUserList(Collection<Long> ids) {
        List<AdminUserDO> users = userService.getUserList(ids);
        return UserConvert.INSTANCE.convertList4(users);
    }

    @Override
    public List<AdminUserRespDTO> getUserListByDeptIds(Collection<Long> deptIds) {
        List<AdminUserDO> users = userService.getUserListByDeptIds(deptIds);
        return UserConvert.INSTANCE.convertList4(users);
    }

    @Override
    public List<AdminUserRespDTO> getUserListByPostIds(Collection<Long> postIds) {
        List<AdminUserDO> users = userService.getUserListByPostIds(postIds);
        return UserConvert.INSTANCE.convertList4(users);
    }

    @Override
    public void validateUserList(Collection<Long> ids) {
        userService.validateUserList(ids);
    }


    @Override
    public AdminUserRespDTO getUserByUsername(String name) {
        AdminUserDO user = userService.getUserByUsername(name);
        return UserConvert.INSTANCE.convert4(user);

    }

    @Override
    @DataPermission(enable = false)
    public boolean checkDeptRole(Long userId) {
        List<String> roleCodeList = roleApi.getRoleCodeList(userId);
        if (!roleCodeList.contains("super_admin")) {
            return false;
        }
        AdminUserDO user = userService.getUser(userId);
        Long superUserId = deptService.getSuperUserId(user.getDeptId());
        if (Objects.equals(userId, superUserId)) {
            return true;
        }
        return false;
    }
}
