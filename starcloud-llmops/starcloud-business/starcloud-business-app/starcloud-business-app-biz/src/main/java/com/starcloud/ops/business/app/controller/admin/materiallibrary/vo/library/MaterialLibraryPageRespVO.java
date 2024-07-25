package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 素材知识库 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MaterialLibraryPageRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "26459")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "图标链接", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @ExcelProperty("图标链接")
    private String iconUrl;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
    @ExcelProperty("描述")
    private String description;

    @Schema(description = "素材类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "素材类型", converter = DictConvert.class)
    @DictFormat("material_format_type")
    private Integer formatType;

    @Schema(description = "素材库类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer libraryType;

    @Schema(description = "素材库大小", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("素材库大小")
    private Long allFileSize;

    @Schema(description = "文件数量", example = "2")
    private Long fileCount;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态")
    private Boolean status;

    @Schema(description = "创建人编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("创建人编号")
    private Long creator;

    @Schema(description = "创建人", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("创建人")
    private String createName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;


}