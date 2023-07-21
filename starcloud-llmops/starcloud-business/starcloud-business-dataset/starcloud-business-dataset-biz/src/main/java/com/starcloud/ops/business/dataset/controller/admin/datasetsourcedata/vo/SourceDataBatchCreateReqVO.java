package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 数据集源数据创建 Request VO")
@Data
@ToString(callSuper = true)
public class SourceDataBatchCreateReqVO {

    @Schema(description = " 存储 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String storageId;

    @Schema(description = " 资源名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String  sourceName;

    @Schema(description = "字符数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long wordCount;

}