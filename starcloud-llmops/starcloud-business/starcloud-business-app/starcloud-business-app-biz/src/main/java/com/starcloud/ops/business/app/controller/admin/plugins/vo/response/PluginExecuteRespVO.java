package com.starcloud.ops.business.app.controller.admin.plugins.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(description = "插件执行结果")
public class PluginExecuteRespVO {

    @Schema(description = "执行状态")
    private String status;

    @Schema(description = "输出")
    private Object output;

    private Integer createdAt;

    private Integer completedAt;



}
