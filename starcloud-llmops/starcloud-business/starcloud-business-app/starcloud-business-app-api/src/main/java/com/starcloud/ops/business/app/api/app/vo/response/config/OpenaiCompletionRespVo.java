package com.starcloud.ops.business.app.api.app.vo.response.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "openai参数配置")
public class OpenaiCompletionRespVo {

    @Schema(description = "返回内容的令牌最大数量")
    private Integer maxTokens;

    @Schema(description = "多样性")
    private Double temperature;

    @Schema(description = "采样范围")
    private Double topP;

    @Schema(description = "词汇控制")
    private Double presencePenalty;

    @Schema(description = "重复控制")
    private Double frequencyPenalty;

    @Schema(description = "回复消息数")
    private Integer n = 1;

    @Schema(description = "流式响应")
    private Boolean stream ;
}
