package com.starcloud.ops.business.app.controller.admin.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.app.dto.AppCategoryDTO;
import com.starcloud.ops.business.app.api.app.dto.AppDTO;
import com.starcloud.ops.business.app.api.app.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.request.AppPublishRequest;
import com.starcloud.ops.business.app.api.app.request.AppRequest;
import com.starcloud.ops.business.app.api.app.request.AppUpdateRequest;
import com.starcloud.ops.business.app.api.market.request.AppMarketUidRequest;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用管理接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@RestController
@RequestMapping("/llm/app")
@Tag(name = "星河云海-应用管理", description = "星河云海应用管理")
public class AppController {

    @Resource
    private AppService appService;

    @GetMapping("/categories")
    @Operation(summary = "查询应用类别列表", description = "查询应用类别列表")
    @ApiOperationSupport(order = 8, author = "nacoyer")
    public CommonResult<List<AppCategoryDTO>> categories() {
        return CommonResult.success(appService.categories());
    }

    @GetMapping("/recommends")
    @Operation(summary = "查询推荐的应用列表", description = "查询推荐的应用列表")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<List<AppDTO>> recommends() {
        return CommonResult.success(appService.listRecommendedApps());
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询我的应用列表", description = "分页查询我的应用列表")
    @ApiOperationSupport(order = 12, author = "nacoyer")
    public CommonResult<PageResp<AppDTO>> page(@Validated AppPageQuery query) {
        return CommonResult.success(appService.page(query));
    }

    @GetMapping("/get")
    @Operation(summary = "根据 UID 获得应用", description = "根据 UID 获取应用详情")
    @ApiOperationSupport(order = 14, author = "nacoyer")
    public CommonResult<AppDTO> get(@Parameter(name = "uid", description = "应用 UID") @RequestParam("uid") String uid) {
        return CommonResult.success(appService.getByUid(uid));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用", description = "创建一个新的应用")
    @ApiOperationSupport(order = 16, author = "nacoyer")
    public CommonResult<Boolean> create(@Validated @RequestBody AppRequest request) {
        appService.create(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/copy")
    @Operation(summary = "复制应用", description = "复制一个应用")
    @ApiOperationSupport(order = 18, author = "nacoyer")
    public CommonResult<Boolean> copy(@Validated @RequestBody AppRequest request) {
        appService.copy(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PutMapping("/modify")
    @Operation(summary = "更新应用", description = "根据 UID 更新应用")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<Boolean> modify(@Validated @RequestBody AppUpdateRequest request) {
        appService.modify(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用", description = "根据 UID 删除应用")
    @ApiOperationSupport(order = 22, author = "nacoyer")
    public CommonResult<Boolean> delete(@Parameter(name = "uid", description = "应用 UID") @RequestParam("uid") String uid) {
        appService.deleteByUid(uid);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/publish")
    @Operation(summary = "发布应用到应用市场", description = "发布应用到应用市场")
    @ApiOperationSupport(order = 24, author = "nacoyer")
    public CommonResult<Boolean> publish(@Validated @RequestBody AppPublishRequest request) {
        appService.publicAppToMarket(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/verifyHasDownloaded")
    @Operation(summary = "校验应用是否已经下载过", description = "校验应用是否已经下载过")
    @ApiOperationSupport(order = 26, author = "nacoyer")
    public CommonResult<Boolean> verifyHasDownloaded(@RequestBody AppMarketUidRequest request) {
        return CommonResult.success(appService.verifyHasDownloaded(request.getMarketUid()));
    }
}
