package com.starcloud.ops.business.user.enums.dept;

import com.google.common.collect.Sets;
import com.starcloud.ops.business.user.pojo.dto.PermissionDTO;
import com.starcloud.ops.business.user.pojo.dto.PermissionOption;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.starcloud.ops.business.user.enums.ErrorCodeConstant.DEPT_ROLE_NOT_EXIST;

@Getter
public enum UserDeptRoleEnum {

    NORMAL(100, "普通用户", Sets.newHashSet(
    )),

    ADMIN(500, "管理员", Sets.newHashSet(
            DeptPermissionEnum.app_edit.getPermission(),
            DeptPermissionEnum.plugin_edit.getPermission(),
            DeptPermissionEnum.plugin_bind_add.getPermission(),
            DeptPermissionEnum.notification_edit.getPermission(),
            DeptPermissionEnum.mission_edit.getPermission(),
            DeptPermissionEnum.material_library_edit.getPermission(),
            DeptPermissionEnum.material_library_column_edit.getPermission(),
            DeptPermissionEnum.material_library_slice_edit.getPermission(),

            DeptPermissionEnum.material_library_delete.getPermission(),
            DeptPermissionEnum.material_library_column_delete.getPermission(),
            DeptPermissionEnum.material_library_slice_delete.getPermission()
    )),

    SUPER_ADMIN(1000, "创建者", Sets.newHashSet(
            DeptPermissionEnum.app_delete.getPermission(),
            DeptPermissionEnum.app_edit.getPermission(),
            DeptPermissionEnum.plugin_delete.getPermission(),
            DeptPermissionEnum.plugin_edit.getPermission(),
            DeptPermissionEnum.plugin_bind_add.getPermission(),
            DeptPermissionEnum.plugin_bind_delete.getPermission(),
            DeptPermissionEnum.notification_delete.getPermission(),
            DeptPermissionEnum.notification_edit.getPermission(),
            DeptPermissionEnum.notification_publish.getPermission(),
            DeptPermissionEnum.mission_edit.getPermission(),
            DeptPermissionEnum.mission_delete.getPermission(),

            DeptPermissionEnum.material_library_edit.getPermission(),
            DeptPermissionEnum.material_library_delete.getPermission(),

            DeptPermissionEnum.material_library_column_edit.getPermission(),
            DeptPermissionEnum.material_library_column_delete.getPermission(),

            DeptPermissionEnum.material_library_slice_edit.getPermission(),
            DeptPermissionEnum.material_library_slice_delete.getPermission()
    )),
    ;


    private final Integer roleCode;

    private final String desc;

    private final Set<String> permissions;

    UserDeptRoleEnum(Integer roleCode, String desc, Set<String> permissions) {
        this.roleCode = roleCode;
        this.desc = desc;
        this.permissions = permissions;
    }

    public static UserDeptRoleEnum getByRoleCode(Integer roleCode) {
        for (UserDeptRoleEnum value : UserDeptRoleEnum.values()) {
            if (value.getRoleCode().equals(roleCode)) {
                return value;
            }
        }
        throw exception(DEPT_ROLE_NOT_EXIST);
    }

    /**
     * 获取类型枚举
     *
     * @return 类型枚举
     */
    public static List<PermissionOption> options() {
        return Arrays.stream(values()).sorted(Comparator.comparingInt(UserDeptRoleEnum::ordinal))
                .map(item -> {
                    PermissionOption option = new PermissionOption();
                    option.setLabel(item.getDesc());
                    option.setValue(item.getRoleCode());
                    List<PermissionDTO> permission = DeptPermissionEnum.getPermission(item.getPermissions());
                    option.setPermissionList(permission);
                    return option;
                }).collect(Collectors.toList());
    }
}
