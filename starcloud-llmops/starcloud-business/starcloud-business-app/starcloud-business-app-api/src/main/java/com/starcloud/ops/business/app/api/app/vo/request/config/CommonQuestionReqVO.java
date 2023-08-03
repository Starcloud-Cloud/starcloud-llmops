package com.starcloud.ops.business.app.api.app.vo.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "常用问题")
@NoArgsConstructor
public class CommonQuestionReqVO {

    @Schema(description = "常用问题")
    private String content;

    @Schema(description = "开启")
    private Boolean enabled;
}
