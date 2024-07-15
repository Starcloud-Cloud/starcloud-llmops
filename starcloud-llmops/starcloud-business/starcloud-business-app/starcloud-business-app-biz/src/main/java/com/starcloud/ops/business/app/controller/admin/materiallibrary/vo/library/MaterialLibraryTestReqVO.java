package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnSaveReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 素材知识库新增/修改 Request VO")
@Data
public class MaterialLibraryTestReqVO {


    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "图标链接", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    private List<MaterialLibraryTableColumnSaveReqVO> saveReqVOS;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private List<Map<String, Object>> materialList;

}