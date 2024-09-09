package com.starcloud.ops.business.app.api.xhs.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "小红书笔记详情")
public class NoteDetail {

    @Schema(description = "互动信息")
    private NoteInteractInfo interactInfo;

    @Schema(description = "ip地址")
    private String ipLocation;

    @Schema(description = "笔记id")
    private String noteId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String desc;

    private LocalDateTime time;

    private LocalDateTime lastUpdateTime;

    @Schema(description = "标签")
    private List<NoteTag> tagList;

    @Schema(description = "图片")
    private List<NoteImage> imageList;

    @Schema(description = "视频")
    private NoteVideo video;
}
