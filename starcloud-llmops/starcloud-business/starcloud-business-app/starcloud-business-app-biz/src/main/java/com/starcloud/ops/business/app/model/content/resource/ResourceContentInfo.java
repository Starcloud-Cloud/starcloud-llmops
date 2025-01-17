package com.starcloud.ops.business.app.model.content.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
@Schema(description = "资源内容信息")
public class ResourceContentInfo implements Serializable {

    private static final long serialVersionUID = 2497111460729040498L;

    /**
     * 完整的视频链接
     */
    @Schema(description = "完整的视频链接")
    private String completeVideoUrl;

    /**
     * 完整的音频链接
     */
    @Schema(description = "完整的音频链接")
    private String completeAudioUrl;

    /**
     * 图片PDF链接
     */
    @Schema(description = "图片PDF链接")
    private String imagePdfUrl;

    /**
     * 单词pdf链接
     */
    @Schema(description = "单词pdf链接")
    private String wordPdfUrl;
}
