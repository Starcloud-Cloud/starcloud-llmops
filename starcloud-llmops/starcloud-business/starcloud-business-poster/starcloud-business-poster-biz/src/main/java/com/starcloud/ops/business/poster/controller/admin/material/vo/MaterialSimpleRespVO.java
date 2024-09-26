package com.starcloud.ops.business.poster.controller.admin.material.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 海报素材 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MaterialSimpleRespVO {

    @Schema(description = "主键id", requiredMode = Schema.RequiredMode.REQUIRED, example = "18992")
    @ExcelProperty("主键id")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10885")
    @ExcelProperty("编号")
    private String uid;

    @Schema(description = "素材数据", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("素材数据")
    private String materialData;

    @Schema(description = "请求数据")
    @ExcelProperty("请求数据")
    private String requestParams;


}