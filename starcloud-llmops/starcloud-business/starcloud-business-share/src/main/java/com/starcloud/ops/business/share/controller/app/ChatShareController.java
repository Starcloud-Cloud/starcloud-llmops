package com.starcloud.ops.business.share.controller.app;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatSkillVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageRespVO;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.app.service.chat.ChatSkillService;
import com.starcloud.ops.business.share.controller.app.vo.ChatReq;
import com.starcloud.ops.business.share.service.ChatShareService;
import com.starcloud.ops.business.share.util.EndUserCodeUtil;
import com.starcloud.ops.business.user.service.impl.EndUserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.Cookie;
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
@RequestMapping("/share/chat")
@Tag(name = "魔法AI-分享聊天")
public class ChatShareController {

    @Autowired
    private ChatSkillService chatSkillService;

    @Autowired
    private EndUserServiceImpl endUserService;

    @Resource
    private ChatShareService chatShareService;

    @Resource
    private ChatService chatService;

    @GetMapping("/detail/{mediumUid}")
    @Operation(summary = "聊天应用详情")
    @PermitAll
    public CommonResult<AppRespVO> detail(@PathVariable("mediumUid") String mediumUid,
                                          @CookieValue(value = "fSId", required = false) String upfSId,
                                          HttpServletRequest request, HttpServletResponse response) {
        upfSId = EndUserCodeUtil.parseUserCodeAndSaveCookie(upfSId, request, response);
        endUserService.webLogin(upfSId);
        return CommonResult.success(chatShareService.chatShareDetail(mediumUid));
    }

    @GetMapping("/history")
    @Operation(summary = "聊天应用详情")
    public CommonResult<PageResult<LogAppMessageRespVO>> histroy(@CookieValue(value = "conversationUid",required = false) String conversationUid,
                                                                 @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        if (StringUtils.isBlank(conversationUid)) {
            return CommonResult.success(new PageResult<LogAppMessageRespVO>());
        }
        PageResult<LogAppMessageDO> pageResult = chatService.chatHistory(conversationUid, pageNo, pageSize);
        return CommonResult.success(LogAppMessageConvert.INSTANCE.convertPage(pageResult));
    }

    @PostMapping("/conversation")
    @Operation(summary = "聊天执行")
    @PermitAll
    public SseEmitter execute(@RequestBody ChatRequestVO chatRequestVO,
                              @CookieValue(value = "conversationUid", required = false) String conversationUid,
                              @CookieValue(value = "fSId", required = false) String upfSId,
                              HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-transform");
        response.setHeader("X-Accel-Buffering", "no");
        upfSId = EndUserCodeUtil.parseUserCodeAndSaveCookie(upfSId, request, response);
        String endUserId = endUserService.webLogin(upfSId);
        if (StringUtils.isBlank(conversationUid)) {
            conversationUid = IdUtil.fastSimpleUUID();
            Cookie cookie = new Cookie("conversationUid", conversationUid);
            cookie.setMaxAge(365 * 24 * 60 * 60);
            response.addCookie(cookie);
        }
        chatRequestVO.setConversationUid(conversationUid);

        SseEmitter emitter = new SseEmitter(60000L);
        chatRequestVO.setSseEmitter(emitter);
        chatRequestVO.setEndUser(endUserId);
        chatShareService.shareChat(chatRequestVO);
        return emitter;
    }


    @Operation(summary = "聊天上下文技能列表")
    @GetMapping("/skill")
    public CommonResult<List<ChatSkillVO>> chatSkill(@RequestParam(value = "appUid") String appUid) {

        List<ChatSkillVO> chatSkillVOS = chatSkillService.chatSkill(appUid);

        return CommonResult.success(chatSkillVOS);
    }
}
