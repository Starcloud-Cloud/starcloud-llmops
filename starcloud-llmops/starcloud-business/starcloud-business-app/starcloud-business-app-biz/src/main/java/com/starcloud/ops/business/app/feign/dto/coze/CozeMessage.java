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
public class CozeMessage implements java.io.Serializable {

    private static final long serialVersionUID = -8079201254049880553L;

    /**
     * 发送这条消息的实体。取值：
     * <ol>
     *     <li>user：代表该条消息内容是用户发送的。</li>
     *     <li>assistant：代表该条消息内容是 Bot 发送的。</li>
     * </ol>
     */
    @JsonProperty("role")
    private String role;

    /**
     * 消息类型。默认为 question。
     * <ol>
     *     <li>question：用户输入内容。</li>
     *     <li>answer：Bot 返回给用户的消息内容，支持增量返回。如果工作流绑定了 messge 节点，可能会存在多 answer 场景，此时可以用流式返回的结束标志来判断所有 answer 完成。</li>
     *     <li>function_call：Bot 对话过程中调用函数（function call）的中间结果。</li>
     *     <li>tool_output：调用工具 （function call）后返回的结果。</li>
     *     <li>tool_response：调用工具 （function call）后返回的结果。</li>
     *     <li>follow_up：如果在 Bot 上配置打开了用户问题建议开关，则会返回推荐问题相关的回复内容。</li>
     *     <li>verbose：多 answer 场景下，服务端会返回一个 verbose 包，对应的 content 为 JSON 格式，content.msg_type =generate_answer_finish 代表全部 answer 回复完成。</li>
     * </ol>
     */
    @JsonProperty("type")
    private String type;

    /**
     * 消息的内容，支持纯文本、多模态（文本、图片、文件混合输入）、卡片等多种类型的内容。
     * <ol>
     *     <li>content_type 为 object_string 时，content 为 object_string object 数组序列化之后的 JSON String，详细说明可参考 ObjectString object。</li>
     *     <li>当 content_type = text 时，content 为普通文本，例如 "content" :"Hello!"。</li>
     * </ol>
     */
    @JsonProperty("content")
    private String content;

    /**
     * 消息内容的类型，支持设置为：
     * <ol>
     *     <li>text：文本。</li>
     *     <li>object_string：多模态内容，即文本和文件的组合、文本和图片的组合。</li>
     *     <li>card：卡片。此枚举值仅在接口响应中出现，不支持作为入参。</li>
     * </ol>
     */
    @JsonProperty("content_type")
    private String contentType;

    /**
     * 创建消息时的附加消息，用于传入使用方的自定义数据，获取消息时也会返回此附加消息。<br>
     * 自定义键值对: 应指定为 Map 对象格式。长度为 16 对键值对 <br>
     * 其中键（key）的长度范围为 1～64 个字符，<br>
     * 值（value）的长度范围为 1～512 个字符。
     */
    @JsonProperty("meta_data")
    private Map<String, String> metaData;
}
