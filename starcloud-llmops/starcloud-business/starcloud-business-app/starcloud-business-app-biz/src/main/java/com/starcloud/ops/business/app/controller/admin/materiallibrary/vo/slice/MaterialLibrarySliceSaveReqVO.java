package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.URL;
import retrofit2.http.Url;

import java.util.*;
import javax.validation.constraints.*;
import java.util.*;

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

    @Schema(description = " 链接", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @URL(message = "上传数据的格式必须是 URL")
    private String url;

    @Schema(description = "是否开启数据共享", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Boolean isShare;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "状态不能为空")
    private Boolean status;


    @Schema(description = "列属性")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableContent {
        /**
         * 列名
         */
        @Schema(description = " 列 ID", example = " 1")
        private String columnId;
        /**
         * 描述
         */
        @Schema(description = "值", example = "具体的值")
        private String value;
    }

}