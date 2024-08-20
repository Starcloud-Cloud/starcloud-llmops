package com.starcloud.ops.business.app.controller.admin.plugins.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "素材明细")
public class PluginTestRespVO {

    @Schema(description = "输入")
    private Map<String, Object> arguments;

    @Schema(description = "输出")
    private List<Map<String, Object>> output;

}
