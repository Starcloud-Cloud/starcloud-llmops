package com.starcloud.ops.business.app.controller.admin.comment.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 媒体评论列表 Request VO")
@Data
@ToString(callSuper = true)
public class MediaCommentsListReqVO {

    @Schema(description = "账号类型（1-小红书，2-抖音）", example = "2")
    private String accountType;

    @Schema(description = "操作类型（0-小回复，1-点赞，2-关注）", example = "2")
    private String actionType;

    @Schema(description = "回复状态", example = "1")
    private Integer status;

    @Schema(description = "预计执行开始时间", example = "2")
    private LocalDateTime estimatedExecutionStartTime;

    @Schema(description = "预计执行结束时间", example = "2")
    private LocalDateTime estimatedExecutionEndTime;

}