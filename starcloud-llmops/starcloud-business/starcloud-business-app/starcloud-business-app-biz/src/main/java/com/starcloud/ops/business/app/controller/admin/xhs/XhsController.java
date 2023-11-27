package com.starcloud.ops.business.app.controller.admin.xhs;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsAppExecuteResponse;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsBathImageExecuteRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.XhsImageExecuteResponse;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.limit.AppLimitRequest;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import com.starcloud.ops.business.app.service.xhs.XhsService;
import com.starcloud.ops.framework.common.api.util.SseEmitterUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
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
@RequestMapping("/llm/app/xhs")
@Tag(name = "星河云海-小红书", description = "星河云海应用执行, 应用和应用市场执行")
public class XhsController {

    @Resource
    private XhsService xhsService;

    @Resource
    private AppLimitService appLimitService;

    @GetMapping("/imageTemplates")
    @Operation(summary = "获取图片模板列表")
    public CommonResult<List<XhsImageTemplateDTO>> imageTemplates() {
        return CommonResult.success(xhsService.imageTemplates());
    }

    @PostMapping("/app/execute")
    @Operation(summary = "获取应用信息")
    public CommonResult<List<XhsAppExecuteResponse>> execute(@Validated @RequestBody XhsAppExecuteRequest executeRequest) {
        return CommonResult.success(xhsService.appExecute(executeRequest));
    }

    @PostMapping(value = "/appExecute")
    @Operation(summary = "小红书应用执行")
    public SseEmitter appExecute(@Validated @RequestBody XhsAppExecuteRequest executeRequest, HttpServletResponse httpServletResponse) {
        // 设置响应头
        httpServletResponse.setHeader(AppConstants.CACHE_CONTROL, AppConstants.CACHE_CONTROL_VALUE);
        httpServletResponse.setHeader(AppConstants.X_ACCEL_BUFFERING, AppConstants.X_ACCEL_BUFFERING_VALUE);
        // 设置 SSE
        SseEmitter emitter = SseEmitterUtil.ofSseEmitterExecutor(5 * 60000L, "xhs app");

        executeRequest.setSseEmitter(emitter);
        // WEB_ADMIN 场景
        executeRequest.setScene(AppSceneEnum.XHS_WRITING.name());
        // 执行限流
        AppLimitRequest limitRequest = AppLimitRequest.of(executeRequest.getUid(), executeRequest.getScene());
        if (!appLimitService.appLimit(limitRequest, emitter)) {
            return emitter;
        }
        // 异步执行应用
        xhsService.asyncAppExecute(executeRequest);
        return emitter;
    }

    @PostMapping(value = "/bathImageExecute")
    @Operation(summary = "小红书图片批量执行")
    public CommonResult<List<XhsImageExecuteResponse>> bathImageExecute(@Validated @RequestBody XhsBathImageExecuteRequest request) {
        return CommonResult.success(xhsService.bathImageExecute(request));
    }

}
