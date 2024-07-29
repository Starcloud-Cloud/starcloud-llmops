package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 素材库复制 Request VO")
@Data
@ToString(callSuper = true)
public class MaterialLibraryCopyReqVO {


    @Schema(description = "素材库编号", example = " 1")
    @NotNull(message = "素材库编号不可以为空")
    private Long id;

    @Schema(description = "素材库名称", example = " 素材库名称")
    @NotBlank(message = "素材库名称不可以为空")
    private String name;

    @Schema(description = "是否全部复制", example = "true")
    @NotNull(message = "是否全部复制不可以为空")
    private Boolean copyAll;
}