package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "ai识别")
public class AiIdentifyReqVO {

    @Schema(description = "插件名称")
    @NotBlank(message = "插件名称不能为空")
    private String pluginName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "用户自定义提示词")
    private String userPrompt;

    @Schema(description = "用户输入")
    private String userInput;

    @Schema(description = "输入")
    private String input;

    @Schema(description = "输入结构")
    private String inputFormart;
}
