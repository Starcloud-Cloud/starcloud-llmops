package com.starcloud.ops.business.app.api.xhs.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "互动信息")
public class NoteInteractInfo {

    @Schema(description = "点赞数")
    private Integer likedCount;

    @Schema(description = "收藏数")
    private Integer collectedCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "分享数")
    private Integer shareCount;
}
