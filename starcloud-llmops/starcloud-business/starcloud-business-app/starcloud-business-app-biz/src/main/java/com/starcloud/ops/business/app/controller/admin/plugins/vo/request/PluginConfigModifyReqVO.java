package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import com.starcloud.ops.business.app.controller.admin.plugins.vo.PluginDefinitionVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改插件配置")
public class PluginConfigModifyReqVO extends PluginDefinitionVO {

    private String uid;
}
