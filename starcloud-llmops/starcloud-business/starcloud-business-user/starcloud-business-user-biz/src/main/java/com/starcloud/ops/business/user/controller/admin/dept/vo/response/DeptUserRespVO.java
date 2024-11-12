package com.starcloud.ops.business.user.controller.admin.dept.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "部门下绑定的用户")
public class DeptUserRespVO {

    @Schema(description = "用户绑定id")
    private Long userDeptId;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "部门中用户的角色")
    private String deptRole;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "email")
    private String email;

    @Schema(description = "消耗总魔法豆")
    private Long costPoints = 0L;

    @Schema(description = "消耗总图片数")
    private Long imageCount = 0L;

    @Schema(description = "矩阵魔法豆")
    private Long matrixBeanCounts;
}
