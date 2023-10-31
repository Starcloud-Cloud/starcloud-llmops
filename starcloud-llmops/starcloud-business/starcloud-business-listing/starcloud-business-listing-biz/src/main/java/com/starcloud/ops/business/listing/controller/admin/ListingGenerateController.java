package com.starcloud.ops.business.listing.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.listing.service.ListingGenerateService;
import com.starcloud.ops.business.listing.vo.ListingGenerateRequest;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.util.SseEmitterUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Listing 生成 控制器
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-27
 */
@RestController
@RequestMapping("/llm/listing/execute")
@Tag(name = "星河云海-Listing生成", description = "星河云海Listing生成")
public class ListingGenerateController {

    @Resource
    private ListingGenerateService listingGenerateService;

    @PostMapping("/metadata")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    @Operation(summary = "Listing 生成元数据")
    public CommonResult<Map<String, List<Option>>> metadata() {
        return CommonResult.success(listingGenerateService.metadata());
    }

    @PostMapping("/execute")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    @Operation(summary = "同步生成Listing标题或者五点描述或者产品描述等", description = "同步生成Listing标题或者五点描述或者产品描述等")
    public CommonResult<AppExecuteRespVO> execute(@Validated @RequestBody ListingGenerateRequest request) {
        return CommonResult.success(listingGenerateService.execute(request));
    }

    @PostMapping("/asyncExecute")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    @Operation(summary = "异步生成Listing标题或者五点描述或者产品描述等", description = "异步生成Listing标题或者五点描述或者产品描述等")
    public SseEmitter asyncExecute(@Validated @RequestBody ListingGenerateRequest request, HttpServletResponse httpServletResponse) {
        // 设置响应头
        httpServletResponse.setHeader(AppConstants.CACHE_CONTROL, AppConstants.CACHE_CONTROL_VALUE);
        httpServletResponse.setHeader(AppConstants.X_ACCEL_BUFFERING, AppConstants.X_ACCEL_BUFFERING_VALUE);
        // 设置 SSE
        SseEmitter emitter = SseEmitterUtil.ofSseEmitterExecutor(5 * 60000L, "listing");
        request.setSseEmitter(emitter);

        // 异步执行应用
        listingGenerateService.asyncExecute(request);
        return emitter;
    }

}
