package com.starcloud.ops.business.app.api.app.vo.response.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "聊天模型参数配置")
public class ModelConfigRespVO {

    /**
     * 提供者  ： openai
     */
    @Schema(description = "提供者",example = "openai")
    private String provider;

    /**
     * 模型配置
     */
    @Schema(description = "模型配置",example = "gpt-3.5-turbo")
    private OpenaiCompletionRespVo completionParams;
}
