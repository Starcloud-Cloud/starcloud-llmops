package com.starcloud.ops.business.job.biz.controller.admin.vo.request;

import com.starcloud.ops.business.job.biz.controller.admin.vo.BusinessJobBaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "修改定时任务")
public class BusinessJobModifyReqVO extends BusinessJobBaseVO {

    @Schema(description = "定时任务uid")
    @NotBlank(message = "uid不能为空")
    private String uid;
}
