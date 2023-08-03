package com.starcloud.ops.business.app.api.app.vo.response.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "openai参数配置")
public class OpenaiCompletionRespVo {

    @Schema(description = "语言模型", example = "gpt-3.5-turbo")
    private String model = "gpt-3.5-turbo";

    @Schema(description = "返回内容的令牌最大数量")
    private Integer maxTokens = 500;

    @Schema(description = "多样性")
    private Double temperature = 0.7d;

    @Schema(description = "采样范围")
    private Double topP = 1d;

    @Schema(description = "词汇控制")
    private Double presencePenalty = 0d;

    @Schema(description = "重复控制")
    private Double frequencyPenalty = 0d;

    @Schema(description = "回复消息数")
    private Integer n = 1;

    @Schema(description = "流式响应")
    private Boolean stream = true;
}
