package com.starcloud.ops.business.app.feign;

import com.starcloud.ops.business.app.feign.config.CozeFeignConfiguration;
import com.starcloud.ops.business.app.feign.dto.coze.CozeChatResult;
import com.starcloud.ops.business.app.feign.dto.coze.CozeMessageResult;
import com.starcloud.ops.business.app.feign.request.coze.CozeChatRequest;
import com.starcloud.ops.business.app.feign.response.CozeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@FeignClient(name = "${feign.remote.coze.name:coze}",
        url = "${feign.remote.coze.url:https://api.coze.cn}",
        path = "/v3",
        configuration = CozeFeignConfiguration.class)
public interface CozeClient {

    /**
     * 扣子机器人聊天
     *
     * @param conversationId 会话 ID，即会话的唯一标识。
     * @param request        请求参数
     * @return 回话结果
     */
    @PostMapping("/chat")
    CozeResponse<CozeChatResult> chat(@RequestParam(value = "conversation_id", required = false) String conversationId,
                                      @Validated @RequestBody CozeChatRequest request);

    /**
     * 扣子机器人查询聊天
     *
     * @param conversationId 会话 ID，即会话的唯一标识。
     * @param chatId         聊天 ID，即聊天的唯一标识。
     * @return 回话结果
     */
    @GetMapping("/chat/retrieve")
    CozeResponse<CozeChatResult> retrieve(@RequestParam(value = "conversation_id") String conversationId,
                                          @RequestParam(value = "chat_id") String chatId);

    /**
     * 扣子机器人聊天查询消息
     *
     * @param conversationId 会话 ID，即会话的唯一标识。
     * @param chatId         聊天 ID，即聊天的唯一标识。
     * @return 消息列表
     */
    @GetMapping("/chat/message/list")
    CozeResponse<List<CozeMessageResult>> messageList(@RequestParam(value = "conversation_id") String conversationId,
                                                      @RequestParam(value = "chat_id") String chatId);
}
