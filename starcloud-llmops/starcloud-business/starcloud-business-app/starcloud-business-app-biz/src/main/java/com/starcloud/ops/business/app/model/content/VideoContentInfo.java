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

    @Schema(description = "生成的视频的会话uid")
    private String conversationUid;

    @Schema(description = "合并成功")
    private Boolean merged;

    @Schema(description = "合并错误信息")
    private String mergeMsg;
}
