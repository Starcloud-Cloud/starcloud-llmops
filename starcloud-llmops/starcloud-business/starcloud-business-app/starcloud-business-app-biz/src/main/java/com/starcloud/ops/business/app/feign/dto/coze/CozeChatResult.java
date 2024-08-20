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
public class CozeChatResult implements java.io.Serializable {

    private static final long serialVersionUID = -2263361700708212478L;

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
     * 对话创建的时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
     */
    @JsonProperty("created_at")
    private Integer createdAt;

    /**
     * 对话结束的时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
     */
    @JsonProperty("completed_at")
    private Integer completedAt;

    /**
     * 对话失败的时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
     */
    @JsonProperty("failed_at")
    private Integer failedAt;

    /**
     * 会话的运行状态。取值为：
     * <ol>
     *     <li>created：会话已创建。</li>
     *     <li>in_progress：Bot 正在处理中。</li>
     *     <li>completed：Bot 已完成处理，本次会话结束。</li>
     *     <li>failed：会话失败。</li>
     *     <li>requires_action：会话中断，需要进一步处理。</li>
     * </ol>
     */
    @JsonProperty("status")
    private String status;

    /**
     * 创建消息时的附加消息，用于传入使用方的自定义数据，获取消息时也会返回此附加消息。<br>
     * 自定义键值对: 应指定为 Map 对象格式。长度为 16 对键值对 <br>
     * 其中键（key）的长度范围为 1～64 个字符，<br>
     * 值（value）的长度范围为 1～512 个字符。
     */
    @JsonProperty("meta_data")
    private Map<String, String> metaData;

    /**
     * 对话运行异常时，此字段中返回详细的错误信息。<br>
     * 对话正常运行时，此字段返回 null <br>
     * suggestion 失败不会被标记为运行异常，不计入 lastError。
     */
    @JsonProperty("last_error")
    private CozeLastError lastError;

    /**
     * 需要运行的信息详情。
     */
    @JsonProperty("required_action")
    private CozeRequiredAction requiredAction;

    /**
     * Token 消耗的详细信息。实际的 Token 消耗以对话结束后返回的值为准。
     */
    @JsonProperty("usage")
    private CozeUsage usage;

}
