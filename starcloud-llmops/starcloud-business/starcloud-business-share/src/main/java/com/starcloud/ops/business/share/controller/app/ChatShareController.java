package com.starcloud.ops.business.share.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatSkillVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.chat.ChatSkillService;
import com.starcloud.ops.business.share.controller.app.vo.ChatReq;
import com.starcloud.ops.business.share.util.EndUserCodeUtil;
import com.starcloud.ops.business.user.service.impl.EndUserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 应用执行
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-26
 */
@RestController
@RequestMapping("/s/chat")
@Tag(name = "魔法AI-分享聊天")
public class ChatShareController {

    @Autowired
    private ChatSkillService chatSkillService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private EndUserServiceImpl endUserService;

    @GetMapping("/")
    @Operation(summary = "聊天详情")
    @Parameter(name = "uid", description = "应用uuid", required = true)
    @PermitAll
    public String detail(@RequestParam(name = "uid") String appUid, @CookieValue(value = "fSId", required = false) String upfSId, HttpServletRequest request, HttpServletResponse response) {

        upfSId = EndUserCodeUtil.parseUserCodeAndSaveCookie(upfSId, request, response);

        endUserService.webLogin(upfSId);

        return upfSId;
    }

    @PostMapping("/")
    @Operation(summary = "聊天执行")
    @PermitAll
    public SseEmitter execute(@RequestBody ChatRequestVO chatRequestVO, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-transform");
        httpServletResponse.setHeader("X-Accel-Buffering", "no");

        SseEmitter emitter = new SseEmitter(60000L);

        chatRequestVO.setSseEmitter(emitter);

        chatService.chat(chatRequestVO);

        //appWorkflowService.fireByApp(executeReqVO.getAppUid(), AppSceneEnum.WEB_ADMIN, executeReqVO.getAppReqVO(), executeReqVO.getStepId(), executeReqVO.getConversationUid(), emitter);
        return emitter;
    }


    @Operation(summary = "聊天上下文技能列表")
    @GetMapping("/skill")
    public CommonResult<List<ChatSkillVO>> chatSkill(@RequestParam(value = "appUid") String appUid) {

        List<ChatSkillVO> chatSkillVOS = chatSkillService.chatSkill(appUid);

        return CommonResult.success(chatSkillVOS);
    }
}
