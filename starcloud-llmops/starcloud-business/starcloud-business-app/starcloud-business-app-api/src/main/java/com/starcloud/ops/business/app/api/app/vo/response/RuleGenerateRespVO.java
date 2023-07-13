package com.starcloud.ops.business.app.api.app.vo.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


@Data
@Schema(description = "聊天模式自动编排")
public class RuleGenerateRespVO {

    @Schema(description = "开场白")
    private String openingStatement;

    @Schema(description = "对话前提示词")
    private String prompt;

    @Schema(description = "变量")
    private List<String> variables;
}
