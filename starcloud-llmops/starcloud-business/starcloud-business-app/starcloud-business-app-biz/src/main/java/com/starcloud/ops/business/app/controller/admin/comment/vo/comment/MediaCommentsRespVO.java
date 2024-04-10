package com.starcloud.ops.business.app.controller.admin.comment.vo.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 媒体评论 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MediaCommentsRespVO {

    @Schema(description = "评论编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "5411")
    @ExcelProperty("评论编号")
    private Long id;

    @Schema(description = "账号类型（10-小红书，20-抖音）", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("账号类型（10-小红书，20-抖音）")
    private String accountType;

    @Schema(description = "账号 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("账号 ID")
    private String accountCode;

    @Schema(description = "账号名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("账号名称")
    private String accountName;

    @Schema(description = "账号头像", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("账号头像")
    private String accountAvatar;

    @Schema(description = "媒体编号")
    @ExcelProperty("媒体编号")
    private String mediaCode;

    @Schema(description = "媒体标题")
    @ExcelProperty("媒体标题")
    private String mediaTitle;

    @Schema(description = "媒体封面")
    @ExcelProperty("媒体封面")
    private String mediaCover;

    @Schema(description = "评论人用户编号")
    @ExcelProperty("评论人用户编号")
    private String commentUserCode;

    @Schema(description = "评论人用户昵称", example = "张三")
    @ExcelProperty("评论人用户昵称")
    private String commentUserName;

    @Schema(description = "评论人用户头像")
    @ExcelProperty("评论人用户头像")
    private String commentUserAvatar;

    @Schema(description = "评论内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("评论内容")
    private String commentContent;


    @Schema(description = "评论编号")
    @ExcelProperty("评论编号")
    private String commentCode;

    @Schema(description = "回复状态", example = "1")
    @ExcelProperty("回复状态")
    private Integer responseStatus;

    @Schema(description = "回复类型", example = "1")
    @ExcelProperty("回复类型")
    private Integer likeStatus;

    @Schema(description = "回复内容", example = "2")
    @ExcelProperty("回复内容")
    private Integer concernStatus;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    /**
     * 操作 数组
     */
    private List<MediaCommentsPageReqVO.Action> actions;

    @Schema(description = "用户 App - 商品 SPU 明细的 SKU 信息")
    @Data
    public static class Action {

        @Schema(description = "操作编号", example = "1")
        private Long id;

        @Schema(description = "策略编号", example = "1")
        private Long strategyId;

        @Schema(description = "操作类型", example = "1")
        private Integer actionType;

        @Schema(description = "执行类型 (手动/自动)", example = "1")
        private Integer executeType;

        @Schema(description = "执行内容", example = "1")
        private String executeContent;

        @Schema(description = "执行对象", example = "1")
        private String executeObject;

        @Schema(description = "执行时间", example = "1234")
        private LocalDateTime executeTime;

    }

}