package com.starcloud.ops.business.app.controller.admin.limit;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitModifyReqVO;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitQuery;
import com.starcloud.ops.business.app.api.limit.vo.request.AppPublishLimitReqVO;
import com.starcloud.ops.business.app.api.limit.vo.response.AppPublishLimitRespVO;
import com.starcloud.ops.business.app.service.limit.AppPublishLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-25
 */
@RestController
@RequestMapping("/llm/app/publish/limit")
@Tag(name = "星河云海-应用发布限制", description = "星河云海应用发布限制")
public class AppPublishLimitController {

    @Resource
    private AppPublishLimitService appPublishLimitService;

    @GetMapping("/get")
    @Operation(summary = "获取应用发布限流信息", description = "获取应用发布限流信息")
    @ApiOperationSupport(order = 5, author = "nacoyer")
    public CommonResult<AppPublishLimitRespVO> get(@Validated AppPublishLimitQuery query) {
        return CommonResult.success(appPublishLimitService.defaultIfNull(query));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用发布限流信息", description = "创建应用发布限流信息")
    @ApiOperationSupport(order = 5, author = "nacoyer")
    public CommonResult<String> create(@Validated @RequestBody AppPublishLimitReqVO request) {
        appPublishLimitService.create(request);
        return CommonResult.success("创建应用发布限流成功");
    }

    @PostMapping("/modify")
    @Operation(summary = "修改应用发布限流信息", description = "修改应用发布限流信息")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<String> modify(@Validated @RequestBody AppPublishLimitModifyReqVO request) {
        appPublishLimitService.modify(request);
        return CommonResult.success("修改应用发布限流成功");
    }

}
