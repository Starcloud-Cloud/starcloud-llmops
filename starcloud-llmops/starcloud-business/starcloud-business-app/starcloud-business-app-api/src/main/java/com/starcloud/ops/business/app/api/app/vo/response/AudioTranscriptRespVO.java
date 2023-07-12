package com.starcloud.ops.business.app.api.app.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author starcloud
 */

@Data
@Schema(description = "语音转文字")
@AllArgsConstructor
public class AudioTranscriptRespVO {

    @Schema(description = "语音转文字结果")
    private String text;
}
