package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 素材知识库数据新增/修改 Request VO")
@Data
public class SliceMigrationReqVO extends MaterialLibraryAppReqVO {

    @Schema(description = "素材编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "19427")
    private List<MaterialLibraryTableColumnSaveReqVO> tableColumnSaveReqVOS;

    @Schema(description = "素材编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "19427")
    private List<Map<String, Object>> materialList;


}