package com.starcloud.ops.business.app.feign.dto.video.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "VideoRecordResult", description = "视频生成配置类")
public class VideoMergeResult implements java.io.Serializable {

    private static final long serialVersionUID = -1181017995368474414L;

    /**
     * 任务编号
     */
    @Schema(description = "任务编号")
    private String id;

    /**
     * 视频地址
     */
    @Schema(description = "视频地址")
    private String url;
    /**
     * 音频地址
     */
    @Schema(description = "音频地址")
    private String audio_url;


}
