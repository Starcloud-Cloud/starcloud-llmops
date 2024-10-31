package com.starcloud.ops.business.app.feign;


import com.starcloud.ops.business.app.feign.dto.coze.*;
import com.starcloud.ops.business.app.feign.request.coze.CozeChatRequest;
import com.starcloud.ops.business.app.feign.request.coze.CozeWorkflowRequest;
import com.starcloud.ops.business.app.feign.response.CozeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "${feign.remote.coze.name:coze-public}",
        url = "${feign.remote.coze.url:https://api.coze.cn}")
public interface CozePublicClient {

    /**
     * 查询bot配置
     *
     * @param botId
     * @param accessToken
     * @return
     */
    @GetMapping("/v1/bot/get_online_info")
    CozeResponse<CozeBotInfo> botInfo(@RequestParam(value = "bot_id") String botId, @RequestHeader("Authorization") String accessToken);

    /**
     * 查询空间下所有bot
     *
     * @param spaceId
     * @param accessToken
     * @return
     */
    @GetMapping("/v1/space/published_bots_list")
    CozeResponse<BotListInfo> spaceBots(@RequestParam(value = "space_id") String spaceId,
                                        @RequestHeader("Authorization") String accessToken,
                                        @RequestParam(value = "page_size") Integer pageSize, @RequestParam(value = "page_index") Integer pageIndex);

    /**
     * 扣子机器人聊天
     *
     * @param conversationId 会话 ID，即会话的唯一标识。
     * @param request        请求参数
     * @return 回话结果
     */
    @PostMapping("/v3/chat")
    CozeResponse<CozeChatResult> chat(@RequestParam(value = "conversation_id", required = false) String conversationId,
                                      @Validated @RequestBody CozeChatRequest request,
                                      @RequestHeader("Authorization") String accessToken);

    /**
     * 扣子机器人查询聊天
     *
     * @param conversationId 会话 ID，即会话的唯一标识。
     * @param chatId         聊天 ID，即聊天的唯一标识。
     * @return 回话结果
     */
    @GetMapping("/v3/chat/retrieve")
    CozeResponse<CozeChatResult> retrieve(@RequestParam(value = "conversation_id") String conversationId,
                                          @RequestParam(value = "chat_id") String chatId,
                                          @RequestHeader("Authorization") String accessToken);

    /**
     * 扣子机器人聊天查询消息
     *
     * @param conversationId 会话 ID，即会话的唯一标识。
     * @param chatId         聊天 ID，即聊天的唯一标识。
     * @return 消息列表
     */
    @GetMapping("/v3/chat/message/list")
    CozeResponse<List<CozeMessageResult>> messageList(@RequestParam(value = "conversation_id") String conversationId,
                                                      @RequestParam(value = "chat_id") String chatId,
                                                      @RequestHeader("Authorization") String accessToken);

    /**
     * 空间列表
     */


    /**
     * 查询空间列表
     */
    @GetMapping("/v1/workspaces")
    CozeResponse<SpaceListInfo> spaceList(
            @RequestHeader("Authorization") String accessToken,
            @RequestParam(value = "page_size") Integer pageSize,
            @RequestParam(value = "page_index") Integer pageIndex);


    /**
     * 执行工作流
     */
    @PostMapping("/v1/workflow/run")
    CozeResponse<String> runWorkflow(@Validated @RequestBody CozeWorkflowRequest request,
                                             @RequestHeader("Authorization") String accessToken);
}
