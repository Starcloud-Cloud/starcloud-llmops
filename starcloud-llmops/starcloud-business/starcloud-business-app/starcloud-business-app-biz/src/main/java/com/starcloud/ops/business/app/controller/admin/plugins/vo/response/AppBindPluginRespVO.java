package com.starcloud.ops.business.app.controller.admin.plugins.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "应用绑定的插件")
public class AppBindPluginRespVO {

    @Schema(description = "应用绑定的插件集合")
    private List<PluginDetailVO> pluginDetailList;

    public AppBindPluginRespVO(List<PluginDetailVO> pluginDetailList) {
        this.pluginDetailList = pluginDetailList;
    }
}
