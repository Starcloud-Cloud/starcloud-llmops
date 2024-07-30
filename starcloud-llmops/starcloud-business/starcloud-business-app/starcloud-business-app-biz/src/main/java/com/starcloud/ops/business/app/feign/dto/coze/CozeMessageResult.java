package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeMessageResult {

    /**
     * 对话 ID，即对话的唯一标识。
     */
    @JsonProperty("id")
    private String id;

    /**
     * 会话 ID，即会话的唯一标识。
     */
    @JsonProperty("conversation_id")
    private String conversationId;

    /**
     * 要进行会话聊天的 Bot ID。
     */
    @JsonProperty("bot_id")
    private String botId;

    /**
     * 聊天ID
     */
    @JsonProperty("chat_id")
    private String chatId;

    /**
     * 元数据
     */
    @JsonProperty("meta_data")
    private Map<String, String> metaData;

    /**
     * 发送这条消息的实体。取值：
     * user：代表该条消息内容是用户发送的。
     * assistant：代表该条消息内容是 Bot 发送的。
     */
    @JsonProperty("role")
    private String role;

    /**
     * 消息的内容，支持纯文本、多模态（文本、图片、文件混合输入）、卡片等多种类型的内容。
     */
    @JsonProperty("content")
    private String content;

    /**
     * 消息的类型。取值：
     */
    @JsonProperty("content_type")
    private String contentType;

    /**
     * 消息的创建时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
     */
    @JsonProperty("created_at")
    private Integer createdAt;

    /**
     * 消息的更新时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
     */
    @JsonProperty("updated_at")
    private Integer updatedAt;

    /**
     *
     */
    @JsonProperty("type")
    private String type;
}
