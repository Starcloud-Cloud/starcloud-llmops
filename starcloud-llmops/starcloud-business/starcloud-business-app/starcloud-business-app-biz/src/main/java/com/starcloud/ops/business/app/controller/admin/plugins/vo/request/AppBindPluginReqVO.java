package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "应用绑定的插件")
public class AppBindPluginReqVO {

    @Schema(description = "应用uid")
    @NotBlank(message = "应用uid不能为空")
    private String appUid;

    @Schema(description = "执行计划uid")
    @NotBlank(message = "执行计划uid不能为空")
    private String planUid;

    @Schema(description = "来源  APP/MARKET")
    @NotBlank(message = "来源不能为空")
    private String source;

}
