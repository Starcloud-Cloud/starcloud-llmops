package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 素材知识库表格信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MaterialLibraryTableColumnRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "29996")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "素材库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30175")
    @ExcelProperty("素材库ID")
    private Long libraryId;

    @Schema(description = "列名", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("列名")
    private String columnName;

    @Schema(description = "列宽", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("列宽")
    private Integer columnWidth;

    @Schema(description = "列Code", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("列Code")
    private String columnCode;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("类型")
    private Integer columnType;

    @Schema(description = "描述")
    @ExcelProperty("描述")
    private String description;

    @Schema(description = "是否必须", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否必须")
    private Boolean isRequired;

    @Schema(description = "序号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("序号")
    private Long sequence;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "字段是否为分组字段")
    private Boolean isGroupColumn;

    @Schema(description = "创建人", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("创建人")
    private String createName;


    @Schema(description = "创建人", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("创建人")
    private String deptName;



}