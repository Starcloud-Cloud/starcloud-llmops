package com.starcloud.ops.business.poster.controller.admin.materialgroup.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 海报素材分组 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MaterialGroupRespVO {

    @Schema(description = "主键id", requiredMode = Schema.RequiredMode.REQUIRED, example = "19172")
    @ExcelProperty("主键id")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "6110")
    @ExcelProperty("编号")
    private String uid;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("标题")
    private String title;

    @Schema(description = "缩略图", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("缩略图")
    private String thumbnail;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("类型")
    private String type;

    @Schema(description = "标签")
    @ExcelProperty("标签")
    private String materialTags;

    @Schema(description = "开启状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("开启状态")
    private Integer status;

    @Schema(description = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("用户类型")
    private String userType;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}