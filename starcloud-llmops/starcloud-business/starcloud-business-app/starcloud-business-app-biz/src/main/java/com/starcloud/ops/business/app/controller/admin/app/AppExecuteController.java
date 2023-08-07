package com.starcloud.ops.business.app.controller.admin.app;

import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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

    private static final String CACHE_CONTROL = "Cache-Control";

    private static final String CACHE_CONTROL_VALUE = "no-cache, no-transform";

    private static final String X_ACCEL_BUFFERING = "X-Accel-Buffering";

    private static final String X_ACCEL_BUFFERING_VALUE = "no";

    @Resource
    private AppService appService;

    @PostMapping("/app")
    @Operation(summary = "执行应用")
    public SseEmitter execute(@RequestBody AppExecuteReqVO executeReqVO, HttpServletResponse httpServletResponse) {
        // 设置响应头
        handlerHttpServletResponseHeader(httpServletResponse);
        // 设置 SSE
        SseEmitter emitter = new SseEmitter(60000L);
        executeReqVO.setSseEmitter(emitter);
        // WEB_ADMIN 场景
        executeReqVO.setScene(AppSceneEnum.WEB_ADMIN.name());
        // 异步执行应用
        appService.asyncExecute(executeReqVO);
        //appWorkflowService.fireByApp(executeReqVO.getAppUid(), AppSceneEnum.WEB_ADMIN, executeReqVO.getAppReqVO(), executeReqVO.getStepId(), executeReqVO.getConversationUid(), emitter);
        return emitter;
    }

    @PostMapping("/market")
    @Operation(summary = "执行应用市场")
    public SseEmitter executeMarket(@RequestBody AppExecuteReqVO executeReqVO, HttpServletResponse httpServletResponse) {
        // 设置响应头
        handlerHttpServletResponseHeader(httpServletResponse);
        // 设置 SSE
        SseEmitter emitter = new SseEmitter(60000L);
        executeReqVO.setSseEmitter(emitter);
        // WEB_MARKET 场景, 应用市场专用
        executeReqVO.setScene(AppSceneEnum.WEB_MARKET.name());
        // 异步执行应用
        appService.asyncExecute(executeReqVO);
        //appWorkflowService.fireByApp(executeReqVO.getAppUid(), AppSceneEnum.WEB_MARKET, executeReqVO.getAppReqVO(), executeReqVO.getStepId(), executeReqVO.getConversationUid(),emitter);
        return emitter;
    }

    /**
     * 处理HttpServletResponse Header
     *
     * @param httpServletResponse HttpServletResponse
     */
    private void handlerHttpServletResponseHeader(HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader(CACHE_CONTROL, CACHE_CONTROL_VALUE);
        httpServletResponse.setHeader(X_ACCEL_BUFFERING, X_ACCEL_BUFFERING_VALUE);
    }

}
