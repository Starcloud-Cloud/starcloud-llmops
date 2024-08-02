package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.library;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 素材知识库新增/修改 Request VO")
@Data
public class MaterialLibrarySavePlugInConfigReqVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "26459")
    @NotNull(message = "编号ID不能为空")
    private Long id;

    @Schema(description = "插件配置", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private String pluginConfig;

}