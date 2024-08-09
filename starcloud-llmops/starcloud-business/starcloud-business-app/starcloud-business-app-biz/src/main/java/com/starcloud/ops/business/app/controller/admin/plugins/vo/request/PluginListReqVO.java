package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "查询插件列表")
public class PluginListReqVO {

    @Schema(description = "素材库uid")
    private String libraryUid;
}
