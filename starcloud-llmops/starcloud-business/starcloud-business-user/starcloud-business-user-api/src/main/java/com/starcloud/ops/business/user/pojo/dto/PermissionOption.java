package com.starcloud.ops.business.user.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermissionOption implements Serializable {

    private static final long serialVersionUID = 1963454194330393851L;

    @Schema(description = "属性名，用于展示")
    private String label;

    @Schema(description = "属性值，用于传递")
    private Object value;

    @Schema(description = "权限点")
    private List<PermissionDTO> permissionList;
}
