package com.starcloud.ops.business.app.controller.admin.market;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageRespVO;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.framework.common.api.util.SseEmitterUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Tag(name = "星河云海 - 聊天市场")
@RestController
@RequestMapping("/llm/market")
public class ChatMarketController {

    @Autowired
    private ChatService chatService;

    @Operation(summary = "应用市场聊天")
    @PostMapping("/chat")
    public SseEmitter market(@RequestBody @Valid ChatRequestVO request, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-transform");
        httpServletResponse.setHeader("X-Accel-Buffering", "no");

        SseEmitter emitter = SseEmitterUtil.ofSseEmitterExecutor(60000L, "chat");
        request.setSseEmitter(emitter);
        request.setScene(AppSceneEnum.CHAT_MARKET.name());
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        request.setUserId(loginUserId);
        chatService.chat(request);
        return emitter;
    }

    @Operation(summary = "查询会话")
    @GetMapping("/conversation")
    public CommonResult<LogAppConversationRespVO> listConversation(@RequestParam(value = "appUid") String appUid) {
        return CommonResult.success(chatService.getConversation(appUid,AppSceneEnum.CHAT_MARKET.name()));
    }

}
