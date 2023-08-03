package com.starcloud.ops.business.app.api.app.vo.response.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "语音配置")
@NoArgsConstructor
public class AudioTransciptRespVO {

    @Schema(description = "开启")
    private Boolean enabled;

    private String style;

    private String pitch;

    private String speed;
}
