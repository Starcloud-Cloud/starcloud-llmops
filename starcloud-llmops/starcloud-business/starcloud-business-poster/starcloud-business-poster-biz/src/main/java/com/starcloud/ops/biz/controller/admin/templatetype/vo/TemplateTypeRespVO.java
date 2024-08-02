package com.starcloud.ops.biz.controller.admin.templatetype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 海报模板类型 Response VO")
@Data
public class TemplateTypeRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "30814")
    private Long id;

    @Schema(description = "uid", requiredMode = Schema.RequiredMode.REQUIRED, example = "5629")
    private String uid;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "标签", requiredMode = Schema.RequiredMode.REQUIRED)
    private String label;

    @Schema(description = "次序", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer order;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}