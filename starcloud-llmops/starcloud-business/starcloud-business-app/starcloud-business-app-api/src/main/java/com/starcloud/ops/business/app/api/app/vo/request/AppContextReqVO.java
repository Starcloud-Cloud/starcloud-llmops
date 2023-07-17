package com.starcloud.ops.business.app.api.app.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AppContextReqVO {

    @Schema(description = "场景")
    @NotNull(message = "场景Code 不能为空")
    private String scene;

    @Schema(description = "应用ID")
    private String appId;

    @Schema(description = "会话id")
    private String conversationUid;

    @Schema(description = "消息ID")
    private String messageUid;

}
