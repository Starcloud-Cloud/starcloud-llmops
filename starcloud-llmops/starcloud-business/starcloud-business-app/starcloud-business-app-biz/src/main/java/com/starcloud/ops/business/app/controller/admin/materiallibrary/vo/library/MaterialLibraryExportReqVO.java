package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 素材库复制 Request VO")
@Data
@ToString(callSuper = true)
public class MaterialLibraryExportReqVO {

    @Schema(description = "素材库编号", example = " 1")
    @NotNull(message = "素材库编号不可以为空")
    private Long id;

    @Schema(description = "数据编号", example = " 数据编号")
    private List<Long> sliceIds;
}