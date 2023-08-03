package com.starcloud.ops.business.app.api.app.vo.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "聊天欢迎语")
@NoArgsConstructor
public class OpeningStatementReqVO {

    @Schema(description = "欢迎语内容")
    private String statement;

    @Schema(description = "开启")
    private Boolean enabled;

}
