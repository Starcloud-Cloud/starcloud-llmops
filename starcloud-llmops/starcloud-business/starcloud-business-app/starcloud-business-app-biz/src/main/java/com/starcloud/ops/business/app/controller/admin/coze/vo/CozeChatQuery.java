package com.starcloud.ops.business.app.controller.admin.coze.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeChatQuery implements java.io.Serializable {

    private static final long serialVersionUID = 8711306737673943774L;

    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String conversationId;

    /**
     * 聊天ID
     */
    @NotBlank(message = "聊天ID不能为空")
    private String chatId;
}
