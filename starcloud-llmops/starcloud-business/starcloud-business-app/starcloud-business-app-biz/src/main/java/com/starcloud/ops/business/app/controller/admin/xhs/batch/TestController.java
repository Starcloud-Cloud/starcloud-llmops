package com.starcloud.ops.business.app.controller.admin.xhs.batch;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.starcloud.ops.business.app.feign.CozeClient;
import com.starcloud.ops.business.app.feign.request.coze.CozeChatRequest;
import com.starcloud.ops.business.app.feign.response.CozeResponse;
import com.starcloud.ops.business.app.feign.dto.coze.CozeChatResult;
import com.starcloud.ops.business.app.feign.dto.coze.CozeMessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@RestController
@RequestMapping("/llm/coze/test/")
public class TestController {

    @Autowired
    private CozeClient cozeClient;

    @PostMapping("/chat")
    public CommonResult<CozeChatResult> chat(@RequestParam(value = "conversation_id", required = false) String conversationId, @RequestBody CozeChatRequest request) {

        request.setUserId(Objects.requireNonNull(SecurityFrameworkUtils.getLoginUserId()).toString());
        CozeResponse<CozeChatResult> chat = cozeClient.chat(conversationId, request);
        CommonResult<CozeChatResult> result = CommonResult.success(chat.getData());
        result.setCode(chat.getCode());
        result.setMsg(chat.getMsg());
        return result;
    }

    @GetMapping("/chat/retrieve")
    public CommonResult<CozeChatResult> retrieve(@RequestParam(value = "conversation_id") String conversationId, @RequestParam(value = "chat_id") String chatId) {

        CozeResponse<CozeChatResult> retrieve = cozeClient.retrieve(conversationId, chatId);
        CommonResult<CozeChatResult> result = CommonResult.success(retrieve.getData());
        result.setCode(retrieve.getCode());
        result.setMsg(retrieve.getMsg());
        return result;
    }

    @GetMapping("/chat/message/list")
    public CommonResult<List<CozeMessageResult>> list(@RequestParam(value = "conversation_id") String conversationId, @RequestParam(value = "chat_id") String chatId) {
        CozeResponse<List<CozeMessageResult>> list = cozeClient.list(conversationId, chatId);
        CommonResult<List<CozeMessageResult>> result = CommonResult.success(list.getData());
        result.setCode(list.getCode());
        result.setMsg(list.getMsg());
        return result;
    }
}
