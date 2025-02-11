package com.starcloud.ops.business.user.enums.dept;

import com.starcloud.ops.business.user.pojo.dto.PermissionDTO;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 权限点前缀定义在PartEnum中
 */
@Getter
public enum DeptPermissionEnum {
    app_edit("app.edit", "应用编辑"),
    app_delete("app.delete", "应用删除"),
    app_execute("app.execute", "应用执行"),
    app_analyse("app.analyse", "应用分析"),

    plugin_edit("plugin.edit", "插件编辑"),
    plugin_delete("plugin.delete", "插件删除"),

    plugin_bind_add("plugin.bind.add", "新增插件绑定"),
    plugin_bind_delete("plugin.bind.delete", "删除插件绑定"),

    notification_edit("notification.edit", "通告编辑"),
    notification_delete("notification.delete", "通告删除"),
    notification_publish("notification.publish", "通告发布"),

    mission_edit("mission.edit", "通告任务编辑"),
    mission_delete("mission.delete", "通告任务删除"),

    material_library_edit("material.library.edit", "素材库编辑"),
    material_library_delete("material.library.delete", "素材库删除"),

    material_library_column_edit("material.library.column.edit.edit", "素材库字段编辑"),
    material_library_column_delete("material.library.column.delete", "素材库字段删除"),

    material_library_slice_edit("material.library.slice.edit", "素材库数据编辑"),
    material_library_slice_delete("material.library.slice.delete", "素材库数据删除"),

    material_group_edit("material.group.edit", "海报编辑"),
    material_group_delete("material.group.delete", "海报删除"),

    ;

    private final String permission;

    private final String desc;

    private static final Map<String, DeptPermissionEnum> PERMISSION_ENUM_MAP = Arrays.stream(DeptPermissionEnum.values()).collect(Collectors.toMap(DeptPermissionEnum::getPermission, Function.identity()));

    DeptPermissionEnum(String permission, String desc) {
        this.permission = permission;
        this.desc = desc;
    }

    public static PermissionDTO getPermission(String code) {
        DeptPermissionEnum deptPermissionEnum = PERMISSION_ENUM_MAP.get(code);
        return new PermissionDTO(deptPermissionEnum.getPermission(), deptPermissionEnum.desc);
    }

    public static List<PermissionDTO> getPermission(Set<String> codes) {
        return codes.stream().map(DeptPermissionEnum::getPermission).collect(Collectors.toList());
    }

    public static List<String> getPermission(PartEnum partEnum) {
        return Arrays.stream(DeptPermissionEnum.values())
                .map(DeptPermissionEnum::getPermission)
                .filter(permission -> permission.startsWith(partEnum.getCode()))
                .collect(Collectors.toList());
    }
}
