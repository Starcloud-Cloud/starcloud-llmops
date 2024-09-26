package com.starcloud.ops.business.poster.controller.admin.material.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 海报素材 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MaterialRespVO {

    @Schema(description = "主键id", requiredMode = Schema.RequiredMode.REQUIRED, example = "18992")
    @ExcelProperty("主键id")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "10885")
    @ExcelProperty("编号")
    private String uid;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("标题")
    private String title;

    @Schema(description = "缩略图", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("缩略图")
    private String thumbnail;

    @Schema(description = "描述")
    @ExcelProperty("描述")
    private String introduction;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("类型")
    private String type;

    @Schema(description = "标签")
    @ExcelProperty("标签")
    private String materialTags;

    @Schema(description = "素材数据", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("素材数据")
    private String materialData;

    @Schema(description = "请求数据")
    @ExcelProperty("请求数据")
    private String requestParams;

    @Schema(description = "素材分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "881")
    @ExcelProperty("素材分类编号")
    private Long categoryId;

    @Schema(description = "开启状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("开启状态")
    private Integer status;

    @Schema(description = "分类排序")
    @ExcelProperty("分类排序")
    private Integer sort;

    @Schema(description = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("用户类型")
    private String userType;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;


}