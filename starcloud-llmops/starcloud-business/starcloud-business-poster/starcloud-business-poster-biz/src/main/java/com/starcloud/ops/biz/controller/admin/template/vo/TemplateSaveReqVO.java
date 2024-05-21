package com.starcloud.ops.biz.controller.admin.template.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.util.*;

@Schema(description = "管理后台 - 海报模板新增/修改 Request VO")
@Data
public class TemplateSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "28518")
    private Long id;

    @Schema(description = "uid", requiredMode = Schema.RequiredMode.REQUIRED, example = "20632")
    @NotEmpty(message = "uid不能为空")
    private String uid;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "标签")
    private String label;

    @Schema(description = "详情")
    private String json;

    @Schema(description = "url", example = "https://www.iocoder.cn")
    private String tempUrl;

    @Schema(description = "顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "顺序不能为空")
    private Integer order;

    @Schema(description = "类型uid", example = "9354")
    private String TemplateTypeUid;

    @Schema(description = "参数")
    private String params;

}