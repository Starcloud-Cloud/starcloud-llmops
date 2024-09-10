package com.starcloud.ops.business.job.biz.controller.admin.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "任务日志分页查询")
public class JobLogPageReqVO extends PageParam {

    @Schema(description = "定时任务uid")
    @NotBlank(message = "定时任务uid不能为空")
    private String businessJobUid;
}
