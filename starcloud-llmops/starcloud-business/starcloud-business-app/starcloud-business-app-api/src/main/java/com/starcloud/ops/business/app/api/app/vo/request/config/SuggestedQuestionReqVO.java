package com.starcloud.ops.business.app.api.app.vo.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "聊天建议")
@NoArgsConstructor
public class SuggestedQuestionReqVO {

    private Boolean enable;
}
