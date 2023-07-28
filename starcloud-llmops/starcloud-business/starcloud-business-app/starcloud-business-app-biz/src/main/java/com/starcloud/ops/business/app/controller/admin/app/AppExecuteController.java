package com.starcloud.ops.business.app.controller.admin.app;

import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.factory.AppFactory;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.AppWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 应用执行
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-26
 */
@RestController
@RequestMapping("/llm/app/execute")
@Tag(name = "星河云海-应用执行")
public class AppExecuteController {

    @Resource
    private AppWorkflowService appWorkflowService;

    @PostMapping("/app")
    @Operation(summary = "执行应用")
    public SseEmitter execute(@RequestBody AppExecuteReqVO executeReqVO, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-transform");
        httpServletResponse.setHeader("X-Accel-Buffering", "no");

        SseEmitter emitter = new SseEmitter(60000L);

        executeReqVO.setSseEmitter(emitter);

        executeReqVO.setScene(AppSceneEnum.WEB_ADMIN.name());

        AppEntity app = AppFactory.factory(executeReqVO);

        app.aexecute(executeReqVO);

        //appWorkflowService.fireByApp(executeReqVO.getAppUid(), AppSceneEnum.WEB_ADMIN, executeReqVO.getAppReqVO(), executeReqVO.getStepId(), executeReqVO.getConversationUid(), emitter);
        return emitter;
    }


    @PostMapping("/market")
    @Operation(summary = "执行应用市场")
    public SseEmitter executeMarket(@RequestBody AppExecuteReqVO executeReqVO, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-transform");
        httpServletResponse.setHeader("X-Accel-Buffering", "no");
        SseEmitter emitter = new SseEmitter(60000L);

        executeReqVO.setSseEmitter(emitter);
        executeReqVO.setScene(AppSceneEnum.WEB_MARKET.name());

        AppEntity app = AppFactory.factory(executeReqVO);


        app.aexecute(executeReqVO);

        //appWorkflowService.fireByApp(executeReqVO.getAppUid(), AppSceneEnum.WEB_MARKET, executeReqVO.getAppReqVO(), executeReqVO.getStepId(), executeReqVO.getConversationUid(),emitter);
        return emitter;
    }


}
