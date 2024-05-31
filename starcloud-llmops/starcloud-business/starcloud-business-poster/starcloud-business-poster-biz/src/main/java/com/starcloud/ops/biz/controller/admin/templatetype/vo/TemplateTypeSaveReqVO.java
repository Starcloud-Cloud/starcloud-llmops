package com.starcloud.ops.biz.controller.admin.templatetype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;

@Schema(description = "管理后台 - 海报模板类型新增/修改 Request VO")
@Data
public class TemplateTypeSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "30814")
    private Long id;

    @Schema(description = "uid", requiredMode = Schema.RequiredMode.REQUIRED, example = "5629")
    @NotEmpty(message = "uid不能为空")
    private String uid;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "标签", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "标签不能为空")
    private String label;

    @Schema(description = "次序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "次序不能为空")
    private Integer order;

}