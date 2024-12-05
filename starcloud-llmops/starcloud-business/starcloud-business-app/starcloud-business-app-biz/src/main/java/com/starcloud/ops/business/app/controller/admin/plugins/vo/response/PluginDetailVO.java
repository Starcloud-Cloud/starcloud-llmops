package com.starcloud.ops.business.app.controller.admin.plugins.vo.response;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginConfigVO;
import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginDefinitionVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "插件详情")
public class PluginDetailVO {

    @Schema(description = "插件定义")
    private PluginDefinitionVO pluginDefinition;

    @Schema(description = "插件配置")
    private PluginConfigVO pluginConfig;

    public PluginDetailVO(PluginDefinitionVO pluginDefinition, PluginConfigVO pluginConfig) {
        this.pluginDefinition = pluginDefinition;
        this.pluginConfig = pluginConfig;
    }
}
