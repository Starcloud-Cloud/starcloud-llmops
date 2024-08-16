package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 素材知识库数据新增/修改 Request VO")
@Data
public class MaterialLibrarySliceSaveReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "19427")
    private Long id;

    @Schema(description = "素材库ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30132")
    @NotNull(message = "素材库ID不能为空")
    private Long libraryId;

    @Schema(description = "描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "描述不能为空")
    private List<TableContent> content;

    @Schema(description = "是否开启数据共享", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    // @NotNull(message = "状态不能为空")
    private Boolean isShare;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    // @NotNull(message = "状态不能为空")
    private Boolean status;

    @Schema(description = "字符数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "30132")
    private Long charCount;


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


        /**
         * 扩展数据
         */
        @Schema(description = "数据执行编号", example = " 1")
        private String executeId;
    }

}