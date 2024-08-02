package com.starcloud.ops.business.app.model.coze;

import lombok.Data;

import java.util.Map;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class ChatResult implements java.io.Serializable {

    private static final long serialVersionUID = -7243702673081801457L;

    /**
     * 对话 ID，即对话的唯一标识。
     */
    private String id;

    /**
     * 会话 ID，即会话的唯一标识。
     */
    private String conversationId;

    /**
     * 要进行会话聊天的 Bot ID。
     */
    private String botId;

    /**
     * 对话创建的时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
     */
    private Integer createdAt;

    /**
     * 对话结束的时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
     */
    private Integer completedAt;

    /**
     * 对话失败的时间。格式为 10 位的 Unixtime 时间戳，单位为秒。
     */
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
    private String status;

    /**
     * 创建消息时的附加消息，用于传入使用方的自定义数据，获取消息时也会返回此附加消息。<br>
     * 自定义键值对: 应指定为 Map 对象格式。长度为 16 对键值对 <br>
     * 其中键（key）的长度范围为 1～64 个字符，<br>
     * 值（value）的长度范围为 1～512 个字符。
     */
    private Map<String, String> metaData;

    /**
     * 对话运行异常时，此字段中返回详细的错误信息。<br>
     * 对话正常运行时，此字段返回 null <br>
     * suggestion 失败不会被标记为运行异常，不计入 lastError。
     */
    private LastError lastError;

    /**
     * 需要运行的信息详情。
     */
    private RequiredAction requiredAction;

    /**
     * Token 消耗的详细信息。实际的 Token 消耗以对话结束后返回的值为准。
     */
    private Usage usage;

}
