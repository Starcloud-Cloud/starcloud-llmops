package com.starcloud.ops.business.user.pojo.dto;

import lombok.Data;

@Data
public class PermissionDTO {

    private String permissionCode;

    private String desc;

    public PermissionDTO(String permissionCode, String desc) {
        this.permissionCode = permissionCode;
        this.desc = desc;
    }
}
