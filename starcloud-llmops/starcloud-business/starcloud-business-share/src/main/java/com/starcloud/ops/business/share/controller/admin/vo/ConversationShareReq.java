package com.starcloud.ops.business.share.controller.admin.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "分享聊天记录")
public class ConversationShareReq {

    @Schema(description = "会话Uid")
    @NotBlank
    private String conversationUid;

    @Schema(description = "分享Uid")
    private String shareUid;

    @Schema(description = "启用/禁用")
    private Boolean disable;

    @Schema(description = "有效期 天")
    @Min(value = 1, message = "有效期最少为1天")
    private Long expiresTime;

    private String endUser;

    @Schema(description = "uid")
    private String uid;
}
