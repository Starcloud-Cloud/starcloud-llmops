package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "执行插件入参")
public class PluginExecuteReqVO {

    private String uuid;

    private Object inputParams;

}
