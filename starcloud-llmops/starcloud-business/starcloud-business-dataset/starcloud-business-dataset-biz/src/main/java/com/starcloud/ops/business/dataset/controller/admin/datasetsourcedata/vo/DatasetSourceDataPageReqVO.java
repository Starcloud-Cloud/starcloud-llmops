package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 数据集源数据分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetSourceDataPageReqVO extends PageParam {

    @Schema(description = "数据集ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String datasetId;
}