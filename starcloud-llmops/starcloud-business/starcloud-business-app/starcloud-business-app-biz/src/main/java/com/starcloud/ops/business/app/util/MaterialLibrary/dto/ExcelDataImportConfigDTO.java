package com.starcloud.ops.business.app.util.MaterialLibrary.dto;

import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import lombok.Data;

import java.util.List;

@Data
public class ExcelDataImportConfigDTO {

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 用户编号
     */
    private Long libraryId;

    /**
     * 用户编号
     */
    private List<MaterialLibraryTableColumnRespVO> columnConfig;

}
