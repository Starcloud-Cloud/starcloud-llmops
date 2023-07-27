package com.starcloud.ops.business.share.controller.admin;

import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.share.controller.admin.vo.ChatReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 应用执行
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-26
 */
@RestController
@RequestMapping("/c")
@Tag(name = "星河云海-应用执行")
public class ChatShareController {

    @GetMapping("/")
    @Operation(summary = "应用详情")
    public String detail(@RequestParam(name = "appUid") String appUid, HttpServletRequest request, HttpServletResponse response) {

        request.getSession().setMaxInactiveInterval(3600 * 24 * 15);

        return request.getRequestedSessionId();
    }

    @PostMapping("/")
    @Operation(summary = "执行应用")
    public SseEmitter execute(@RequestBody ChatReq chatReq, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-transform");
        httpServletResponse.setHeader("X-Accel-Buffering", "no");

        SseEmitter emitter = new SseEmitter(60000L);

        AppExecuteReqVO executeReqVO = new AppExecuteReqVO();

        executeReqVO.setSseEmitter(emitter);

        executeReqVO.setScene(AppSceneEnum.WEB_ADMIN.name());

        AppEntity app = AppFactory.factory(executeReqVO);

        app.aexecute(executeReqVO);

        //appWorkflowService.fireByApp(executeReqVO.getAppUid(), AppSceneEnum.WEB_ADMIN, executeReqVO.getAppReqVO(), executeReqVO.getStepId(), executeReqVO.getConversationUid(), emitter);
        return emitter;
    }

}
