package com.starcloud.ops.business.app.controller.admin.comment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "管理后台 - 媒体评论新增/修改 Request VO")
@Data
public class MediaCommentsSaveReqVO {

    @Schema(description = "评论编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "5411")
    private Long id;

    @Schema(description = "账号类型（10-小红书，20-抖音）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "账号类型（10-小红书，20-抖音）不能为空")
    private String accountType;

    @Schema(description = "账号 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "账号 ID不能为空")
    private String accountCode;

    @Schema(description = "账号名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "账号名称不能为空")
    private String accountName;

    @Schema(description = "账号头像", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "账号头像不能为空")
    private String accountAvatar;

    @Schema(description = "媒体标题")
    private String mediaTitle;

    @Schema(description = "媒体封面")
    private String mediaCover;

    @Schema(description = "评论人用户编号")
    private String commentUserCode;

    @Schema(description = "评论人用户昵称", example = "张三")
    private String commentUserName;

    @Schema(description = "评论人用户头像")
    private String commentUserAvatar;

    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "评论内容不能为空")
    private String commentContent;

    @Schema(description = "评论编号")
    private String commentCode;

    @Schema(description = "媒体编号")
    private String mediaCode;

    @Schema(description = "点赞状态", example = "1")
    private Boolean likeStatus;

    @Schema(description = "回复状态", example = "1")
    private Boolean responseStatus;

    @Schema(description = "关注状态", example = "2")
    private Boolean concernStatus;

}