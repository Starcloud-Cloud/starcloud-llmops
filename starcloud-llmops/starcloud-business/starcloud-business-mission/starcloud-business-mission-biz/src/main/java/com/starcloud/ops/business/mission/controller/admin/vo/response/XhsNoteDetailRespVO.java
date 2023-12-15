package com.starcloud.ops.business.mission.controller.admin.vo.response;

import com.starcloud.ops.business.app.api.xhs.note.NoteImage;
import com.starcloud.ops.business.app.api.xhs.note.NoteTag;
import com.starcloud.ops.business.app.api.xhs.note.NoteVideo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "小红书笔记详情")
public class XhsNoteDetailRespVO {

    @Schema(description = "自增id")
    private Long id;

    @Schema(description = "小红书笔记id")
    private String nodeId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String desc;

    @Schema(description = "点赞数")
    private Integer likedCount;

    @Schema(description = "收藏数")
    private Integer collectedCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "分享数")
    private Integer shareCount;

    @Schema(description = "图片")
    private List<NoteImage> imageList;

    @Schema(description = "视频")
    private NoteVideo video;

    @Schema(description = "预结算金额")
    private BigDecimal amount;

    @Schema(description = "标签")
    private List<NoteTag> tagList;

}
