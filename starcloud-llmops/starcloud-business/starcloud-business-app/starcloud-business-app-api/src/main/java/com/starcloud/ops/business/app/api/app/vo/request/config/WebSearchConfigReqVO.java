package com.starcloud.ops.business.app.api.app.vo.request.config;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "联网")
@NoArgsConstructor
public class WebSearchConfigReqVO {

    private Boolean enabled;

    private String whenToUse;

    /**
     * 模型配置
     */
    private String webScope;
}
