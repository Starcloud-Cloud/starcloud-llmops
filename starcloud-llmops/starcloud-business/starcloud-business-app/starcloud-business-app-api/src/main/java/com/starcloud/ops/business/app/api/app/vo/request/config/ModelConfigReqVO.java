package com.starcloud.ops.business.app.api.app.vo.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "聊天模型参数配置")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelConfigReqVO {


    /**
     * 提供者  ： openai
     */
    @Schema(description = "提供者",example = "openai")
    private String provider;

    /**
     * 模型配置
     */
    @Schema(description = "模型配置",example = "gpt-3.5-turbo")
    private OpenaiCompletionReqVo completionParams;
}
