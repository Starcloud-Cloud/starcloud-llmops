package com.starcloud.ops.business.app.feign.request.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.starcloud.ops.business.app.feign.dto.coze.CozeMessage;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeChatRequest implements java.io.Serializable {

    private static final long serialVersionUID = -6171979849294061362L;

    /**
     * 要进行会话聊天的 Bot ID <br>
     * 进入 Bot 的 开发页面，开发页面 URL 中 bot 参数后的数字就是 Bot ID <br>
     * 例如https://www.coze.cn/space/341**** /bot/73428668*****，其中 73428668***** 就是 bot ID
     */
    @JsonProperty("bot_id")
    @NotBlank(message = "botId 不能为空")
    private String botId;

    /**
     * 标识当前与 Bot 交互的用户。<br>
     * 由使用方在业务系统中自行定义、生成与维护。
     */
    @JsonProperty("user_id")
    @NotBlank(message = "userId 不能为空")
    private String userId;

    /**
     * 对话的附加信息。你可以通过此字段传入本次对话中用户的问题。<br>
     * 数组长度限制为 100，即最多传入 100 条消息。
     * <ol>
     *     <li>当 auto_save_history=true 时，messages 会作为消息先添加到会话中，然后作为上下文传给大模型。</li>
     *     <li>当 auto_save_history=false 时，messages 只会作为附加信息传给大模型，messages 和模型返回等本次对话的所有消息均不会添加到会话中。</li>
     * </ol>
     */
    @JsonProperty("additional_messages")
    private List<CozeMessage> messages = new ArrayList<>();

    /**
     * 是否启用流式返回。
     * <ol>
     *     <li>true：采用流式响应。 “流式响应”将模型的实时响应提供给客户端，你可以实时获取服务端返回的对话、消息事件，并在客户端中同步处理、实时展示，也可以在 completed 事件中获取 Bot 最终的回复。</li>
     *     <li>
     *         false：（默认）采用非流式响应。 “非流式响应”是指响应中仅包含本次对话的状态等元数据 <br>
     *         此时应同时开启 auto_save_history，在本次对话处理结束后再查看模型回复等完整响应内容。可以参考以下业务流程：<br>
     *         <ol>
     *             <li>调用发起会话接口，并设置 stream = false，auto_save_history=true，表示使用非流式响应，并记录历史消息。你需要记录会话的 Conversation ID 和 Chat ID，用于后续查看详细信息。</li>
     *             <li>定期轮询查看对话详情接口，直到会话状态流转为终态，即 status 为 completed 或 required_action。</li>
     *             <li>调用查看对话消息详情接口，查询大模型生成的最终结果。</li>
     *         </ol>
     *     </li>
     * </ol>
     */
    @JsonProperty("stream")
    private Boolean stream = false;

    /**
     * Bot 中定义的变量。在 Bot prompt 中设置变量 {{key}} 后，可以通过该参数传入变量值，同时支持 Jinja2 语法
     */
    @JsonProperty("custom_variables")
    private Map<String, String> variables = new HashMap<>();

    /**
     * 是否自动保存历史对话记录：
     * <ol>
     *     <li>true：（默认）保存此次模型回复结果和模型执行中间结果。</li>
     *     <li>false：系统不保存历史对话记录，后续无法查看本次对话的基础信息或消息详情。</li>
     * </ol>
     */
    @JsonProperty("auto_save_history")
    private Boolean autoSaveHistory = true;

    /**
     * 创建消息时的附加消息，获取消息时也会返回此附加消息。<br>
     * 自定义键值对，应指定为 Map 对象格式。长度为 16 对键值对，<br>
     * 其中键（key）的长度范围为 1～64 个字符，值（value）的长度范围为 1～512 个字符。
     */
    @JsonProperty("meta_data")
    private Map<String, String> metaData = new HashMap<>();
}
