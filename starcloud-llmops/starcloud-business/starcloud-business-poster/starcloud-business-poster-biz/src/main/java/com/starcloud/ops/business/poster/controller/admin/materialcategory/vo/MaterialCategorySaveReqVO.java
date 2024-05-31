package com.starcloud.ops.business.poster.controller.admin.materialcategory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;

@Schema(description = "管理后台 - 素材分类新增/修改 Request VO")
@Data
public class MaterialCategorySaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "9776")
    private Long id;

    @Schema(description = "父分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20467")
    @NotNull(message = "父分类编号不能为空")
    private Long parentId;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "分类名称不能为空")
    private String name;

    @Schema(description = "缩略图", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "缩略图不能为空")
    private String thumbnail;

    @Schema(description = "分类排序")
    private Integer sort;

}