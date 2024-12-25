package com.starcloud.ops.business.app.controller.admin.execute;

import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppTestExecuteReqVO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.limit.AppLimitRequest;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import com.starcloud.ops.framework.common.api.util.SseEmitterUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
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
@Tag(name = "星河云海-应用执行", description = "星河云海应用执行, 应用和应用市场执行")
public class AppExecuteController {

    @Resource
    private AppService appService;

    @Resource
    private AppLimitService appLimitService;

    @PostMapping("/app")
    @Operation(summary = "执行应用")
    public SseEmitter execute(@RequestBody AppExecuteReqVO executeRequest, HttpServletResponse httpServletResponse) {
        // 设置响应头
        httpServletResponse.setHeader(AppConstants.CACHE_CONTROL, AppConstants.CACHE_CONTROL_VALUE);
        httpServletResponse.setHeader(AppConstants.X_ACCEL_BUFFERING, AppConstants.X_ACCEL_BUFFERING_VALUE);
        // 设置 SSE
        SseEmitter emitter = SseEmitterUtil.ofSseEmitterExecutor(5 * 60000L, "app");

        executeRequest.setSseEmitter(emitter);
        // WEB_ADMIN 场景
        executeRequest.setScene(AppSceneEnum.WEB_ADMIN.name());
        // 执行限流
        AppLimitRequest limitRequest = AppLimitRequest.of(executeRequest.getAppUid(), executeRequest.getScene());
        if (!appLimitService.appLimit(limitRequest, emitter)) {
            return emitter;
        }
        // 异步执行应用
        appService.asyncExecute(executeRequest);
        return emitter;
    }

    @PostMapping(value = "/market", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    @Operation(summary = "执行应用市场")
    public SseEmitter executeMarket(@RequestBody AppExecuteReqVO executeRequest, HttpServletResponse httpServletResponse) {
        // 设置响应头
        httpServletResponse.setHeader(AppConstants.CACHE_CONTROL, AppConstants.CACHE_CONTROL_VALUE);
        httpServletResponse.setHeader(AppConstants.X_ACCEL_BUFFERING, AppConstants.X_ACCEL_BUFFERING_VALUE);
        // 设置 SSE
        SseEmitter emitter = SseEmitterUtil.ofSseEmitterExecutor(5 * 60000L, "market");
        executeRequest.setSseEmitter(emitter);
        // WEB_MARKET 场景, 应用市场专用
        if (StringUtils.isBlank(executeRequest.getScene())) {
            executeRequest.setScene(AppSceneEnum.WEB_MARKET.name());
        }

        // 执行限流
        AppLimitRequest limitRequest = AppLimitRequest.of(executeRequest.getAppUid(), executeRequest.getScene());
        if (!appLimitService.marketLimit(limitRequest, emitter)) {
            return emitter;
        }

        // 异步执行应用
        appService.asyncExecute(executeRequest);
        return emitter;
    }

    @PostMapping("/test")
    @Operation(summary = "测试执行应用")
    public SseEmitter test(@RequestBody AppTestExecuteReqVO executeRequest, HttpServletResponse httpServletResponse) {
        // 设置响应头
        httpServletResponse.setHeader(AppConstants.CACHE_CONTROL, AppConstants.CACHE_CONTROL_VALUE);
        httpServletResponse.setHeader(AppConstants.X_ACCEL_BUFFERING, AppConstants.X_ACCEL_BUFFERING_VALUE);
        // 设置 SSE
        SseEmitter emitter = SseEmitterUtil.ofSseEmitterExecutor(5 * 60000L, "app-test");

        executeRequest.setSseEmitter(emitter);
        // WEB_ADMIN 场景
        executeRequest.setScene(AppSceneEnum.APP_TEST.name());
        // 执行限流
        AppLimitRequest limitRequest = AppLimitRequest.of(executeRequest.getAppUid(), executeRequest.getScene());
        if (!appLimitService.appLimit(limitRequest, emitter)) {
            return emitter;
        }
        // 异步执行应用
        appService.executeTest(executeRequest);
        return emitter;
    }

}
