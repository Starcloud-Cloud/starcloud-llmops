package com.starcloud.ops.biz.controller.admin.materialcategory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 素材分类 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MaterialCategoryRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "9776")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "父分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "20467")
    @ExcelProperty("父分类编号")
    private Long parentId;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("分类名称")
    private String name;

    @Schema(description = "缩略图", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("缩略图")
    private String thumbnail;

    @Schema(description = "分类排序")
    @ExcelProperty("分类排序")
    private Integer sort;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}