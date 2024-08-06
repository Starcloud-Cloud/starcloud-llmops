package com.starcloud.ops.business.app.controller.admin.plugins.vo.response;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginDefinitionVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "插件配置")
public class PluginRespVO extends PluginDefinitionVO {

    @Schema(description = "uid")
    private String uid;

    @Schema(description = "是否发布")
    private Boolean published;
}
