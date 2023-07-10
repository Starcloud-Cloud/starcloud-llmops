package com.starcloud.ops.business.app.api.chat;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author starcloud
 */
@Schema(description = "聊天请求")
@Data
public class ChatRequest {

    @Schema(description = "会话id")
    @NotNull(message = "会话id 不能为空")
    private String conversationId;

    @Schema(description = "聊天参数")
    private Map<String,String> inputs;

    @Schema(description = "聊天内容")
    @NotBlank(message = "聊天内容 不能为空")
    private String query;

}
