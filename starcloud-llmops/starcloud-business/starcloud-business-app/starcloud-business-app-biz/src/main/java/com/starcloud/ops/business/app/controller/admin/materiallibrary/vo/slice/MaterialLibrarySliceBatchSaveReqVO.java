package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.slice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "管理后台 - 素材知识库数据新增/修改 Request VO")
@Data
public class MaterialLibrarySliceBatchSaveReqVO {

    @Schema(description = "数据不能为空", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据不能为空")
    private List<MaterialLibrarySliceSaveReqVO> saveReqVOS;
}