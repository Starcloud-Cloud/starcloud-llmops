package com.starcloud.ops.business.app.api.app.vo.response.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "常用问题")
public class CommonQuestionRespVO {
    @Schema(description = "常用问题")
    private String content;

    @Schema(description = "开启")
    private Boolean enabled;
}
