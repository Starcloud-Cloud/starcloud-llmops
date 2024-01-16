package com.starcloud.ops.business.user.controller.admin.dept.vo.request;

import cn.iocoder.yudao.module.system.controller.admin.dept.vo.dept.DeptBaseVO;
import com.starcloud.ops.business.user.controller.admin.dept.vo.dto.DeptConfigDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建部门")
public class CreateDeptReqVO extends DeptBaseVO {

    @Schema(description = "部门描述")
    private String description;

    @Schema(description = "部门配置")
    private DeptConfigDTO config;
}
