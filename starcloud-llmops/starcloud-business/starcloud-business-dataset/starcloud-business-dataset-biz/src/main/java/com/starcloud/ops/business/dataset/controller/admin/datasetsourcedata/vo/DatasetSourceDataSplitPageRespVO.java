package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 数据集源数据分页 Request VO")
@Data
@ToString(callSuper = true)
public class DatasetSourceDataSplitPageRespVO {

    @Schema(description = "源数据ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String datasetId;

    @Schema(description = "源数据ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uid;

    @Schema(description = " 文件 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "位置", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer position;

    @Schema(description = "字数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer wordCount;

    @Schema(description = " hash", requiredMode = Schema.RequiredMode.REQUIRED)
    private String segmentHash;

    @Schema(description = " 状态", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "是否经用", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean disabled;

}