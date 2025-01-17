package com.starcloud.ops.business.app.model.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 */
@Data
public class VideoContentInfo implements Serializable {

    private static final long serialVersionUID = 6371779185161413528L;

    /**
     * 视频
     */
    @Schema(description = "视频列表")
    private List<VideoContent> videoList;

    /**
     * 完整的视频
     */
    @Schema(description = "完整的视频")
    private String completeVideoUrl;

    /**
     * 完整的音频
     */
    @Schema(description = "完整的音频")
    private String completeAudioUrl;
}
