package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice;

import com.alibaba.excel.annotation.ExcelProperty;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 素材知识库数据新增/修改 Request VO")
@Data
public class MaterialLibrarySliceUseRespVO {



    @Schema(description = "表头")
    @ExcelProperty("表头")
    private List<MaterialLibraryTableColumnRespVO> tableMeta;

    @Schema(description = "素材数据")
    @ExcelProperty("素材数据")
    private List<MaterialLibrarySliceRespVO> sliceRespVOS;

}