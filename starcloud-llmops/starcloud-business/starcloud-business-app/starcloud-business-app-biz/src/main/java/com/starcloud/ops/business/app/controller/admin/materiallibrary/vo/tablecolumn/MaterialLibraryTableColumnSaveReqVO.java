package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Schema(description = "管理后台 - 素材知识库表格信息新增/修改 Request VO")
@Data
public class MaterialLibraryTableColumnSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "29996")
    private Long id;

    @Schema(description = "素材库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30175")
    @NotNull(message = "素材库ID不能为空")
    private Long libraryId;

    @Schema(description = "列名", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "列名不能为空")
    @Size(max = 20, message = "列名不能超过20个字符")
    private String columnName;

    @Schema(description = "列宽", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    // @NotEmpty(message = "列宽不能为空")
    private Integer columnWidth;

    @Schema(description = "列Code", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String columnCode;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "类型不能为空")
    private Integer columnType;

    @Schema(description = "描述")
    @Size(max = 200, message = "列名不能超过200个字符")
    private String description;

    @Schema(description = "是否必须", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否必须不能为空")
    private Boolean isRequired;

    @Schema(description = "序号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "序号不能为空")
    private Long sequence;

}