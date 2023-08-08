package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import com.starcloud.ops.business.dataset.pojo.dto.SplitRule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 上传基础 Request VO")
@Data
public class UploadReqVO {

    private Boolean sync;

    private String batch;

    private SplitRule splitRule;

    private String datasetId;

}