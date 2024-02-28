package com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreativePlanBatchPageReqVO extends PageParam {

    @NotBlank(message = "创作计划uid不能为空")
    @Schema(description = "创作计划uid")
    private String planUid;

}
