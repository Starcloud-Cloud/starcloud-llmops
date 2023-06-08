package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import com.starcloud.ops.llm.langchain.core.model.llm.document.SplitRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "管理后台 - 数据集源数据创建 Request VO")
@Data
@ToString(callSuper = true)
public class DatasetSourceDataCreateReqVO {

    @Schema(description = "文件ID", required = true)
    private String filed;

    @Schema(description = "数据集ID", required = true)
    private String datasetId;

    @Schema(description = "数据集处理规则")
    private SplitRule splitRule;

}