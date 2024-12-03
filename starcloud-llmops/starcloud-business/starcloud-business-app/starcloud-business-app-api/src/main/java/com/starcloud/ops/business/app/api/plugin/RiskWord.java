package com.starcloud.ops.business.app.api.plugin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "违禁词详情")
public class RiskWord {

    @Schema(description = "违禁词名称")
    private String title;

    @Schema(description = "风险等级-禁用词/敏感词")
    private String type;

    @Schema(description = "所属行业/平台")
    private String sourse;

    @Schema(description = "违禁原因")
    private String reason;
}
