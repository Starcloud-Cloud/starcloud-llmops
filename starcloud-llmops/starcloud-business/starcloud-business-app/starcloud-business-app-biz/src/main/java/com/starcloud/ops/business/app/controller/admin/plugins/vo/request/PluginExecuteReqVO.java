package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "执行插件入参")
public class PluginExecuteReqVO {

    @Schema(description = "插件uid")
    @NotBlank(message = "插件uid不能为空")
    private String uuid;

    @Schema(description = "执行参数")
    @NotBlank(message = "执行参数不能为空")
    private Object inputParams;

    @Schema(description = "素材库uid")
    @NotBlank(message = "素材库uid不能为空")
    private String libraryUid;

}
