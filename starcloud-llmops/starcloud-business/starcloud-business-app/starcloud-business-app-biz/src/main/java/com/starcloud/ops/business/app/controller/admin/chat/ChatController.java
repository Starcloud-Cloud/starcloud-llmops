package com.starcloud.ops.business.app.controller.admin.chat;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.domain.entity.ChatAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "星河云海 - 对话")
@RestController
@RequestMapping("/llm/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Operation(summary = "聊天")
    @PostMapping("/completions")
    public SseEmitter conversation(@RequestBody @Valid ChatRequestVO request, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-transform");
        httpServletResponse.setHeader("X-Accel-Buffering", "no");

        SseEmitter emitter = new SseEmitter(60000L);
        request.setSseEmitter(emitter);

        ChatAppEntity appEntity = AppFactory.factory(request);

        //appEntity.execute(request);

        appEntity.aexecute(request);

        return emitter;
    }


    @Operation(summary = "聊天历史")
    @GetMapping("/history/{conversationUid}")
    public CommonResult<List<LogAppMessageDO>> history(@PathVariable("conversationUid") String conversationUid) {
        return CommonResult.success(chatService.chatHistory(conversationUid));
    }

    @Operation(summary = "聊天建议")
    @PostMapping("/suggestion/{conversationUid}")
    public CommonResult<List<String>> suggestion(@PathVariable("conversationUid") String conversationUid) {
        return CommonResult.success(chatService.chatSuggestion(conversationUid));
    }

}
