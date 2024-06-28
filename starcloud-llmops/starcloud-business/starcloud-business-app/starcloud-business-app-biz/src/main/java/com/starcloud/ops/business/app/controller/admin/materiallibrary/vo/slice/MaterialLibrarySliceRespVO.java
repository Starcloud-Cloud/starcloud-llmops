package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 素材知识库数据 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MaterialLibrarySliceRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "19427")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = "素材库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30132")
    @ExcelProperty("素材库ID")
    private Long libraryId;

    @Schema(description = "字符数", example = "21593")
    @ExcelProperty("字符数")
    private Long charCount;

    @Schema(description = "总使用次数", requiredMode = Schema.RequiredMode.REQUIRED, example = "17558")
    @ExcelProperty("总使用次数")
    private Long usedCount;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("描述")
    private String content;

    @Schema(description = "序列", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("序列")
    private Long sequence;

    @Schema(description = " 链接", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @ExcelProperty(" 链接")
    private String url;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("common_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Boolean status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}