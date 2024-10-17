package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn.MaterialLibraryTableColumnRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 素材知识库 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MaterialLibraryRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "26459")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "UID", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("UID")
    private String uid;

    @Schema(description = "图标链接", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @ExcelProperty("图标链接")
    private String iconUrl;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED, example = "你猜")
    @ExcelProperty("描述")
    private String description;

    @Schema(description = "素材类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "素材类型", converter = DictConvert.class)
    @DictFormat("material_format_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer formatType;

    @Schema(description = "素材库类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer libraryType;

    @Schema(description = "来源", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @DictFormat("material_create_source")
    private Integer createSource;

    @Schema(description = "素材库大小", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("素材库大小")
    private Long allFileSize;

    @Schema(description = "分享范围", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "分享范围", converter = DictConvert.class)
    @DictFormat("material_share_range") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String shareRange;

    @Schema(description = "总使用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "6606")
    @ExcelProperty("总使用次数")
    private Long totalUsedCount;

    @Schema(description = "插件配置")
    @ExcelProperty("插件配置")
    private String pluginConfig;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态")
    private Boolean status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;


    @Schema(description = "表头")
    @ExcelProperty("表头")
    private List<MaterialLibraryTableColumnRespVO> tableMeta;

    @Schema(description = "是否包含插件")
    private boolean hasPlugin;

}