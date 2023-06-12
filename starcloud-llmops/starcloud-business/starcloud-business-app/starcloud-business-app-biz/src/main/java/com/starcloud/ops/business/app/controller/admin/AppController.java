package com.starcloud.ops.business.app.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.app.dto.AppDTO;
import com.starcloud.ops.business.app.api.app.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.request.AppRequest;
import com.starcloud.ops.business.app.api.app.request.AppUpdateRequest;
import com.starcloud.ops.business.app.service.AppService;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 模版管理接口
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-29
 */
@RestController
@RequestMapping("/llm/app")
@Tag(name = "星河云海-模版管理", description = "星河云海模版管理")
public class AppController {

    @Resource
    private AppService appService;

    @GetMapping("/listRecommended")
    @Operation(summary = "查询推荐的模版列表", description = "查询推荐的模版列表")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<List<AppDTO>> listRecommended() {
        return CommonResult.success(appService.listRecommendedTemplates());
    }

    @GetMapping("/pageDownload")
    @Operation(summary = "分页查询下载的模版列表", description = "分页查询下载的模版列表")
    @ApiOperationSupport(order = 11, author = "nacoyer")
    public CommonResult<PageResp<AppDTO>> pageDownload(@Validated AppPageQuery query) {
        return CommonResult.success(appService.pageDownloadTemplates(query));
    }

    @GetMapping("/pageMyTemplate")
    @Operation(summary = "分页查询我的模版列表", description = "分页查询我的模版列表")
    @ApiOperationSupport(order = 12, author = "nacoyer")
    public CommonResult<PageResp<AppDTO>> pageMyTemplate(@Validated AppPageQuery query) {
        return CommonResult.success(appService.pageMyTemplate(query));
    }

    @GetMapping("/get")
    @Operation(summary = "获得模版", description = "根据 UID 获取模版详情")
    @ApiOperationSupport(order = 13, author = "nacoyer")
    public CommonResult<AppDTO> get(@Parameter(name = "模版 UID") @RequestParam("uid") String uid) {
        return CommonResult.success(appService.getByUid(uid));
    }

    @PostMapping("/create")
    @Operation(summary = "创建模版", description = "创建一个新的模版")
    @ApiOperationSupport(order = 14, author = "nacoyer")
    public CommonResult<Boolean> create(@Validated @RequestBody AppRequest template) {
        return CommonResult.success(appService.create(template));
    }

    @PostMapping("/copy")
    @Operation(summary = "复制模版", description = "复制一个模版")
    @ApiOperationSupport(order = 15, author = "nacoyer")
    public CommonResult<Boolean> copy(@Validated @RequestBody AppRequest template) {
        return CommonResult.success(appService.copy(template));
    }

    @PutMapping("/modify")
    @Operation(summary = "更新模版", description = "根据 UID 更新模版")
    @ApiOperationSupport(order = 16, author = "nacoyer")
    public CommonResult<Boolean> modify(@Validated @RequestBody AppUpdateRequest template) {
        return CommonResult.success(appService.modify(template));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除模版", description = "根据 UID 删除模版")
    @ApiOperationSupport(order = 17, author = "nacoyer")
    public CommonResult<Boolean> delete(@Parameter(name = "模版 UID") @RequestParam("uid") String uid) {
        return CommonResult.success(appService.deleteByUid(uid));
    }

    @PostMapping("/verifyHasDownloaded")
    @Operation(summary = "校验模版是否已经下载过", description = "校验模版是否已经下载过")
    @ApiOperationSupport(order = 18, author = "nacoyer")
    public CommonResult<Boolean> verifyHasDownloaded(@Parameter(name = "模版市场 UID") @RequestParam("marketUid") String marketUid) {
        return CommonResult.success(appService.verifyHasDownloaded(marketUid));
    }
}
