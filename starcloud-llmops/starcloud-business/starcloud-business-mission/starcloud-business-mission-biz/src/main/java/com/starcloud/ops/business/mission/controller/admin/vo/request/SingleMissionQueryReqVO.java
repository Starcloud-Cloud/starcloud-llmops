package com.starcloud.ops.business.mission.controller.admin.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Schema(description = "批量查询任务")
public class SingleMissionQueryReqVO {

    @Schema(description = "任务类型")
    @NotBlank(message = "任务类型不能为空")
    private String singleMissionType;

    @Schema(description = "查询数量")
    @NotNull(message = "查询数量不能为空")
    @Min(value = 1,message = "查询数量要大于0")
    private Integer limitSize;

    @Schema(description = "失败重试")
    private Boolean failRetry;

    @Schema(description = "执行类型")
    private String executeType;

}
