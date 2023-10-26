package com.starcloud.ops.business.share.controller.admin.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "分享链接详情")
public class ConversationShareResp {

    /**
     * uid
     */
    @Schema(description = "uid")
    private String uid;

    /**
     * key
     */
    @Schema(description = "key")
    private String shareKey;

    /**
     * app uid
     */
    @Schema(description = "app uid")
    private String appUid;

    /**
     * 会话uid
     */
    @Schema(description = "会话uid")
    private String conversationUid;

    @Schema(description = "媒介id")
    private String mediumUid;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private LocalDateTime expiresTime;

    /**
     * 启用/禁用
     */
    @Schema(description = "启用/禁用")
    private Boolean disabled;

    /**
     * 游客
     */
    @Schema(description = "游客")
    private Boolean endUser;

    @Schema(description = "邀请码")
    private String inviteCode;
}
