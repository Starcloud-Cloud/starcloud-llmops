package com.starcloud.ops.business.dataset.controller.admin.datasethandlerules.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 数据集规则分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetHandleRulesPageReqVO extends PageParam {

    @Schema(description = " 应用 ID")
    @NotNull(message = "应用 ID 不可以为空")
    private String appId;
}