package com.starcloud.ops.business.user.controller.admin.dept.vo.response;

import cn.iocoder.yudao.module.system.controller.admin.dept.vo.dept.DeptBaseVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.dto.DeptConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Schema(description = "部门信息 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeptRespVO extends DeptBaseVO {

    @Schema(description = "部门编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    private LocalDateTime createTime;

    @Schema(description = "邀请码")
    private String inviteCode;

    @Schema(description = "部门头像")
    private String avatar;

    @Schema(description = "超级管理员id")
    private Long adminUserId;

    @Schema(description = "部门配置")
    private DeptConfigDTO config;

    @Schema(description = "部门描述")
    private String description;

}