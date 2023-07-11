package com.starcloud.ops.business.app.api.app.vo.response.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "开场白")
public class OpeningStatementRespVO {

    @Schema(description = "开场白内容")
    private String statement;

}
