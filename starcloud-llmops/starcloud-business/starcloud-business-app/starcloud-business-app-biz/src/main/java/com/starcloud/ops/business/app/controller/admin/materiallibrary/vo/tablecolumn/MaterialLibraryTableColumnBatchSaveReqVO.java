package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 素材知识库表格信息新增/修改 Request VO")
@Data
public class MaterialLibraryTableColumnBatchSaveReqVO {


    @Schema(description = "素材库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30175")
    @NotNull(message = "素材库ID不能为空")
    private Long libraryId;

    @Schema(description = "表头数据不能为空", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "表头数据不能为空")
    private List<MaterialLibraryTableColumnSaveReqVO> tableColumnSaveReqVOList;


}