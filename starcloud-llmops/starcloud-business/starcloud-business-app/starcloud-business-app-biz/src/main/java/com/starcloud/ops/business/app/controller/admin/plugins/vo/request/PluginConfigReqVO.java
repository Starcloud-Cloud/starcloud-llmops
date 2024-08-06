package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "插件配置")
public class PluginConfigReqVO extends PluginConfigVO {

    @Schema(description = "uid")
    @NotBlank(message = "uid不能为空")
    private String uid;

}
