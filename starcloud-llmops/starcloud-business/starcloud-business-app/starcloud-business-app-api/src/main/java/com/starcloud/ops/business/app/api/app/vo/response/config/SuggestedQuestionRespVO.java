package com.starcloud.ops.business.app.api.app.vo.response.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "聊天建议")
@NoArgsConstructor
public class SuggestedQuestionRespVO {

    @Schema(description = "开启聊天建议")
    private Boolean enabled;
}
