package com.starcloud.ops.business.app.controller.admin.app;

import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.app.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
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

    @Resource
    private AppService appService;

    @PostMapping("/app")
    @Operation(summary = "执行应用")
    public SseEmitter execute(@RequestBody AppExecuteReqVO executeReqVO, HttpServletResponse httpServletResponse) {
        // 设置响应头
        httpServletResponse.setHeader(AppConstants.CACHE_CONTROL, AppConstants.CACHE_CONTROL_VALUE);
        httpServletResponse.setHeader(AppConstants.X_ACCEL_BUFFERING, AppConstants.X_ACCEL_BUFFERING_VALUE);
        // 设置 SSE
        SseEmitter emitter = new SseEmitter(60000L);
        executeReqVO.setSseEmitter(emitter);
        // WEB_ADMIN 场景
        executeReqVO.setScene(AppSceneEnum.WEB_ADMIN.name());
        // 异步执行应用
        appService.asyncExecute(executeReqVO);
        return emitter;
    }

    @PostMapping("/market")
    @Operation(summary = "执行应用市场")
    public SseEmitter executeMarket(@RequestBody AppExecuteReqVO executeReqVO, HttpServletResponse httpServletResponse) {
        // 设置响应头
        httpServletResponse.setHeader(AppConstants.CACHE_CONTROL, AppConstants.CACHE_CONTROL_VALUE);
        httpServletResponse.setHeader(AppConstants.X_ACCEL_BUFFERING, AppConstants.X_ACCEL_BUFFERING_VALUE);
        // 设置 SSE
        SseEmitter emitter = new SseEmitter(60000L);
        executeReqVO.setSseEmitter(emitter);
        // WEB_MARKET 场景, 应用市场专用
        if (StringUtils.isBlank(executeReqVO.getScene())) {
            executeReqVO.setScene(AppSceneEnum.WEB_MARKET.name());
        }
        // 异步执行应用
        appService.asyncExecute(executeReqVO);
        return emitter;
    }

}
