package com.starcloud.ops.business.user.controller.admin.dept.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户绑定的部门")
public class UserDeptRespVO {

    @Schema(description = "部门id")
    private Long deptId;

    @Schema(description = "部门名称")
    private String deptName;

}
