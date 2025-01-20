package com.starcloud.ops.business.app.feign.dto.video;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class VideoRecordResult implements java.io.Serializable {

    private static final long serialVersionUID = -1181017995368474414L;

    /**
     * 任务编号
     */
    @Schema(description = "任务编号")
    private String id;
    /**
     * 任务状态
     */
    @Schema(description = "任务状态")
    private String status;
    /**
     * 视频地址
     */
    @Schema(description = "视频地址")
    private String url;
    /**
     * 进度
     */
    @Schema(description = "进度")
    private String progress;
    /**
     * 阶段
     */
    @Schema(description = "阶段")
    private String stage;

    /**
     * 错误信息
     */
    @Schema(description = "错误信息")
    private String error;

    /**
     * 音频链接
     */
    @Schema(description = "音频链接")
    @JsonProperty("audio_url")
    private String audioUrl;



}
