package com.starcloud.ops.business.app.controller.admin.plugins.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "校验机器人")
public class PluginTestReqVO {

    private String botId;

    private String accessTokenId;

    private String content;

}
