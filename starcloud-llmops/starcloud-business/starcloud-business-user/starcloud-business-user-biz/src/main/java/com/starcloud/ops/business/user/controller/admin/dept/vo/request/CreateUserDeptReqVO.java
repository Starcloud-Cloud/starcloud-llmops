package com.starcloud.ops.business.user.controller.admin.dept.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Schema(description = "新建用户部门绑定")
@Builder
public class CreateUserDeptReqVO {

    private Long deptId;

    private Long userId;

    /**
     * 部门中用户的角色
     */
    private Integer deptRole;

    /**
     * 邀请人
     */
    private Long inviteUser;
}
