package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<TableContent> content;

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

    @Schema(description = "列属性")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableContent {
        /**
         * 列ID
         */
        @Schema(description = "列 ID", example = " 1")
        private Long columnId;

        /**
         * 列ID
         */
        @Schema(description = "列Code", example = " 1")
        private String columnCode;

        /**
         * 列ID
         */
        @Schema(description = "列名称", example = " 1")
        private String columnName;
        /**
         * 值
         */
        @Schema(description = "列值", example = " 1")
        private String value;

        /**
         * 描述
         */
        @Schema(description = "描述", example = " 1")
        private String description;

        /**
         * 标签
         */
        @Schema(description = "标签", example = " 1")
        private List<String> tags;

        /**
         * 扩展数据
         */
        @Schema(description = "扩展数据", example = " 1")
        private String extend;


    }

}