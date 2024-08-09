package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "查询插件执行结果")
public class PluginResultReqVO {


    @Schema(description = "插件执行code")
    @NotBlank(message = "uid不能为空")
    String code;

    @Schema(description = "插件uid")
    @NotBlank(message = "uid不能为空")
    String uuid;


}
