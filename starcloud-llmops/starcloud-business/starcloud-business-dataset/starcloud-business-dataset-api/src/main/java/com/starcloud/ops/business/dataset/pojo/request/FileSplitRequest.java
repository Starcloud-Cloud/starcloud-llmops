package com.starcloud.ops.business.dataset.pojo.request;

import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "文件切割")
public class FileSplitRequest {

    @Schema(description = "数据集Id")
    @NotBlank(message = "datasetId不能为空")
    private String datasetId;

    @Schema(description = "文档Id")
    @NotBlank(message = "documentId不能为空")
    private String documentId;

    private SplitRule splitRule;

}
