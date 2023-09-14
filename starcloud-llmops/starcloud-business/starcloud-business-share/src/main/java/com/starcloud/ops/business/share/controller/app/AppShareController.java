package com.starcloud.ops.business.share.controller.app;

import cn.hutool.core.lang.Assert;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.service.limit.AppLimitRequest;
import com.starcloud.ops.business.app.service.limit.AppLimitService;
import com.starcloud.ops.business.share.service.AppShareService;
import com.starcloud.ops.business.share.util.EndUserCodeUtil;
import com.starcloud.ops.business.user.service.impl.EndUserServiceImpl;
import com.starcloud.ops.framework.common.api.util.SseEmitterUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 应用执行
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-26
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/share/app")
@Tag(name = "星河云海-分享应用", description = "星河云海应用分享")
public class AppShareController {

    @Resource
    private AppShareService appShareService;

    @Resource
    private EndUserServiceImpl endUserService;

    @Resource
    private AppLimitService appLimitService;

    @GetMapping("/detail/{mediumUid}")
    @PermitAll
    @Operation(summary = "应用详情")
    @ApiOperationSupport(order = 1, author = "nacoyer")
    @Parameter(name = "mediumUid", description = "应用分享唯一标识", required = true)
    public CommonResult<AppRespVO> detail(@PathVariable(name = "mediumUid") String mediumUid,
                                          @CookieValue(value = "fSId", required = false) String upfSId,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {

        upfSId = EndUserCodeUtil.parseUserCodeAndSaveCookie(upfSId, request, response);
        endUserService.webLogin(upfSId);
        return CommonResult.success(appShareService.appShareDetail(mediumUid));
    }

    @PostMapping("/execute")
    @PermitAll
    @Operation(summary = "应用执行")
    @ApiOperationSupport(order = 1, author = "nacoyer")
    public SseEmitter execute(@RequestBody AppExecuteReqVO executeRequest,
                              @CookieValue(value = "fSId", required = false) String upfSId,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        // 用户必须存在
        Assert.notNull(executeRequest.getMediumUid(), "应用分享唯一标识不能为空");

        // 设置响应头
        response.setHeader(AppConstants.CACHE_CONTROL, AppConstants.CACHE_CONTROL_VALUE);
        response.setHeader(AppConstants.X_ACCEL_BUFFERING, AppConstants.X_ACCEL_BUFFERING_VALUE);

        // 设置 EndUser
        upfSId = EndUserCodeUtil.parseUserCodeAndSaveCookie(upfSId, request, response);
        String endUserId = endUserService.webLogin(upfSId);
        executeRequest.setEndUser(endUserId);

        // 设置 SSE
        SseEmitter emitter = SseEmitterUtil.ofSseEmitterExecutor(5 * 60000L, "share app");
        executeRequest.setSseEmitter(emitter);

        // 执行限流
        AppLimitRequest limitRequest = AppLimitRequest.of(executeRequest.getMediumUid(), executeRequest.getScene(), executeRequest.getEndUser());
        if (!appLimitService.channelLimit(limitRequest, emitter)) {
            return emitter;
        }
        // 执行应用
        appShareService.shareAppExecute(executeRequest);
        return emitter;
    }


}
