package com.starcloud.ops.biz.controller.admin.element.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;

@Schema(description = "管理后台 - 海报元素新增/修改 Request VO")
@Data
public class ElementSaveReqVO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED, example = "16677")
    private Long id;

    @Schema(description = "uid", requiredMode = Schema.RequiredMode.REQUIRED, example = "2992")
    @NotEmpty(message = "uid不能为空")
    private String uid;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "标签")
    private String label;

    @Schema(description = "详情", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "详情不能为空")
    private String json;

    @Schema(description = "次序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "次序不能为空")
    private Integer order;

    @Schema(description = "类型uid", example = "8858")
    private String elementTypeUid;

    @Schema(description = "url", example = "https://www.iocoder.cn")
    private String elementUrl;

}