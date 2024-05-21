package com.starcloud.ops.biz.controller.admin.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 海报模板 Response VO")
@Data
public class TemplateRespVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "28518")
    private Long id;

    @Schema(description = "uid", requiredMode = Schema.RequiredMode.REQUIRED, example = "20632")
    private String uid;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer status;

    @Schema(description = "标签")
    private String label;

    @Schema(description = "详情")
    private String json;

    @Schema(description = "url", example = "https://www.iocoder.cn")
    private String tempUrl;

    @Schema(description = "顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer order;

    @Schema(description = "类型uid", example = "9354")
    private String TemplateTypeUid;

    @Schema(description = "参数")
    private String params;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}