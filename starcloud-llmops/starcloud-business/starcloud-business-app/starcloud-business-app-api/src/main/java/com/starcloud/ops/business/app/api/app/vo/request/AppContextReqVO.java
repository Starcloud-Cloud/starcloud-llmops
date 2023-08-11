package com.starcloud.ops.business.app.api.app.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class AppContextReqVO implements Serializable {

    @Schema(description = "场景")
    @NotNull(message = "场景Code 不能为空")
    private String scene;

    @Schema(description = "渠道ID")
    @NotNull(message = "渠道ID")
    private String mediumUid;

    @Schema(description = "应用ID")
    @NotNull(message = "应用ID 不能为空")
    private String appUid;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "游客的唯一标识")
    private String endUser;

    @Schema(description = "会话id")
    private String conversationUid;

    @Schema(description = "消息ID")
    private String messageUid;

}
