package com.starcloud.ops.business.app.controller.admin.publish;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.base.vo.request.UidStatusRequest;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishPageReqVO;
import com.starcloud.ops.business.app.api.publish.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishLatestRespVO;
import com.starcloud.ops.business.app.api.publish.vo.response.AppPublishRespVO;
import com.starcloud.ops.business.app.service.publish.AppPublishService;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 应用管理接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@RestController
@RequestMapping("/llm/app/publish")
@Tag(name = "星河云海-应用发布", description = "星河云海应用发布管理")
public class AppPublishController {

    @Resource
    private AppPublishService appPublishService;

    @GetMapping("/pageAdmin")
    @Operation(summary = "市场管理分页查询发布记录", description = "市场管理分页查询发布记录")
    @ApiOperationSupport(order = 1, author = "nacoyer")
    public CommonResult<PageResp<AppPublishRespVO>> pageAdmin(@Validated AppPublishPageReqVO query) {
        query.setIsAdmin(Boolean.TRUE);
        return CommonResult.success(appPublishService.pageAdmin(query));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询发布记录", description = "分页查询发布记录")
    @ApiOperationSupport(order = 5, author = "nacoyer")
    public CommonResult<PageResp<AppPublishRespVO>> page(@Validated AppPublishPageReqVO query) {
        query.setIsAdmin(Boolean.FALSE);
        return CommonResult.success(appPublishService.page(query));
    }

    @GetMapping("/get/{uid}")
    @Operation(summary = "根据 UID 获得发布记录详情", description = "根据 UID 获得发布记录详情")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<AppPublishRespVO> get(@Parameter(name = "uid", description = "发布 UID") @PathVariable("uid") String uid) {
        return CommonResult.success(appPublishService.get(uid));
    }

    @GetMapping("/getLatest/{appUid}")
    @Operation(summary = "根据应用 UID 获得最新的发布记录详情", description = "根据应用 UID 获得最新的发布记录详情")
    @ApiOperationSupport(order = 15, author = "nacoyer")
    public CommonResult<AppPublishLatestRespVO> getLatest(@Parameter(name = "appUid", description = "应用 UID") @PathVariable("appUid") String uid) {
        return CommonResult.success(appPublishService.getLatest(uid));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用发布记录", description = "创建应用发布记录")
    @ApiOperationSupport(order = 25, author = "nacoyer")
    public CommonResult<AppPublishRespVO> create(@Validated @RequestBody AppPublishReqVO request) {
        return CommonResult.success(appPublishService.create(request));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核发布记录", description = "审核发布记录")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<Boolean> audit(@Validated @RequestBody UidStatusRequest request) {
        appPublishService.audit(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/operate")
    @Operation(summary = "操作发布记录", description = "操作发布记录")
    @ApiOperationSupport(order = 35, author = "nacoyer")
    public CommonResult<Boolean> operate(@Validated @RequestBody UidStatusRequest request) {
        appPublishService.operate(request);
        return CommonResult.success(Boolean.TRUE);
    }


}
