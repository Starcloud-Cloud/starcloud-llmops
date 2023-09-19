package com.starcloud.ops.business.app.controller.admin.chat;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.limit.AppLimitRequest;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import com.starcloud.ops.business.log.api.conversation.vo.response.LogAppConversationRespVO;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageRespVO;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.framework.common.api.util.SseEmitterUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.infra.enums.ErrorCodeConstants.FILE_IS_EMPTY;

@Tag(name = "星河云海 - 对话")
@RestController
@RequestMapping("/llm/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Resource
    private AppLimitService appLimitService;

    @Operation(summary = "聊天")
    @PostMapping("/completions")
    public SseEmitter conversation(@RequestBody @Valid ChatRequestVO request, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-transform");
        httpServletResponse.setHeader("X-Accel-Buffering", "no");

        SseEmitter emitter = SseEmitterUtil.ofSseEmitterExecutor(5 * 60000L, "chat");
        request.setSseEmitter(emitter);

        AppLimitRequest limitRequest = AppLimitRequest.of(request.getAppUid(), request.getScene());
//        limitRequest.setExclude(Collections.singletonList("RATE"));
        if (!appLimitService.appLimit(limitRequest, emitter)) {
            return emitter;
        }

        chatService.chat(request);
        return emitter;
    }

    @Operation(summary = "创建聊天应用")
    @PostMapping("/app/create")
    public CommonResult<String> createChatApp(@RequestParam(value = "uid", required = false) String uid, @RequestParam("robotName") String robotName) {
        return CommonResult.success(chatService.createChatApp(uid, robotName));
    }

    @Operation(summary = "查询所有会话")
    @GetMapping("/conversation")
    public CommonResult<List<LogAppConversationRespVO>> listConversation(@RequestParam(value = "scene", defaultValue = "CHAT") String scene,
                                                                         @RequestParam(value = "appUid") String appUid) {
        return CommonResult.success(chatService.listConversation(scene, appUid));
    }


    @Operation(summary = "聊天历史")
    @GetMapping("/history")
    public CommonResult<PageResult<LogAppMessageRespVO>> history(@RequestParam(value = "conversationUid") String conversationUid,
                                                                 @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageResult<LogAppMessageDO> pageResult = chatService.chatHistory(conversationUid, pageNo, pageSize);
        return CommonResult.success(LogAppMessageConvert.INSTANCE.convertPage(pageResult));
    }


    @PostMapping("/avatar/{appUid}")
    @Operation(summary = "修改chatApp头像")
    public CommonResult<String> updateUserAvatar(@PathVariable("appUid") String appUid,
                                                 @RequestParam("avatarFile") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            throw exception(FILE_IS_EMPTY);
        }
        String avatar = chatService.updateAppAvatar(appUid, file);
        return success(avatar);
    }

    @GetMapping("/avatar/default")
    @Operation(summary = "获取推荐头像")
    public CommonResult<List<String>> defaultAvatar() {
        return success(chatService.defaultAvatar());
    }


    @Operation(summary = "聊天建议")
    @PostMapping("/suggestion/{conversationUid}")
    public CommonResult<List<String>> suggestion(@PathVariable("conversationUid") String conversationUid) {
        return CommonResult.success(chatService.chatSuggestion(conversationUid));
    }

}
