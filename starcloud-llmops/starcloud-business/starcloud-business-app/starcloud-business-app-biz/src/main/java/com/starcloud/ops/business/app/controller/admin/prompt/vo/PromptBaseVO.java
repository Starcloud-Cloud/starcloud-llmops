package com.starcloud.ops.business.app.controller.admin.prompt.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Schema(description = "提示词详情")
public class PromptBaseVO {

    @Schema(description = "提示词名称")
    @NotBlank(message = "提示词名称必填")
    @Size(min = 1, max = 30, message = "提示词名称长度为 1-30 个字符")
    private String name;

    @Schema(description = "提示词描述")
    @NotBlank(message = "提示词描述必填")
    @Size(min = 1, max = 30, message = "描述长度为 1-30 个字符")
    private String description;

    @Schema(description = "提示词")
    private String promptText;

    @Schema(description = "推荐提示词")
    private Boolean sysEnable;
}
