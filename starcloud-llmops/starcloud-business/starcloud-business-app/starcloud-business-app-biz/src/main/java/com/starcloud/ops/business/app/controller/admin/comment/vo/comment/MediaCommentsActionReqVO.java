package com.starcloud.ops.business.app.controller.admin.comment.vo.comment;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 媒体操作 Request VO")
@Data
@ToString(callSuper = true)
public class MediaCommentsActionReqVO{

    @Schema(description = "评论编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "5411")
    @ExcelProperty("评论编号")
    private Long id;

    @Schema(description = "操作类型", example = "2")
    private Long actionId;

    @Schema(description = "操作状态", example = "2")
    private Integer actionExecuteType;

    @Schema(description = "执行时间", example = "2")
    private LocalDateTime executeTime;

}