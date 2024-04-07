package com.starcloud.ops.business.app.controller.admin.comment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "管理后台 - 手动回复评论 Request VO")
@Data
public class MediaCommentsManualResponseReqVO {

    @Schema(description = "评论编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "5411")
    private Long id;

    @Schema(description = "回复内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "回复内容不能为空")
    private String responseContent;


}