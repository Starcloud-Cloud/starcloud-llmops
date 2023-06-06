package com.starcloud.ops.business.app.controller.admin.template;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.template.dto.TemplateDTO;
import com.starcloud.ops.business.app.api.template.request.TemplatePageQuery;
import com.starcloud.ops.business.app.api.template.request.TemplateRequest;
import com.starcloud.ops.business.app.api.template.request.TemplateUpdateRequest;
import com.starcloud.ops.business.app.service.template.TemplateService;
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
public class TemplateController {

    @Resource
    private TemplateService templateService;

    @GetMapping("/listRecommended")
    @Operation(summary = "查询推荐的模版列表", description = "查询推荐的模版列表")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<List<TemplateDTO>> listRecommended() {
        return CommonResult.success(templateService.listRecommendedTemplates());
    }

    @GetMapping("/pageDownload")
    @Operation(summary = "分页查询下载的模版列表", description = "分页查询下载的模版列表")
    @ApiOperationSupport(order = 11, author = "nacoyer")
    public CommonResult<PageResp<TemplateDTO>> pageDownload(@Validated TemplatePageQuery query) {
        return CommonResult.success(templateService.pageDownloadTemplates(query));
    }

    @GetMapping("/pageMyTemplate")
    @Operation(summary = "分页查询我的模版列表", description = "分页查询我的模版列表")
    @ApiOperationSupport(order = 12, author = "nacoyer")
    public CommonResult<PageResp<TemplateDTO>> pageMyTemplate(@Validated TemplatePageQuery query) {
        return CommonResult.success(templateService.pageMyTemplate(query));
    }

    @GetMapping("/get")
    @Operation(summary = "获得模版", description = "根据 ID 获取模版详情")
    @ApiOperationSupport(order = 13, author = "nacoyer")
    public CommonResult<TemplateDTO> get(@Parameter(name = "模版 ID") @RequestParam("id") Long id) {
        return CommonResult.success(templateService.getById(id));
    }

    @PostMapping("/create")
    @Operation(summary = "创建模版", description = "创建一个新的模版")
    @ApiOperationSupport(order = 14, author = "nacoyer")
    public CommonResult<Boolean> create(@Validated @RequestBody TemplateRequest template) {
        return CommonResult.success(templateService.create(template));
    }

    @PostMapping("/copy")
    @Operation(summary = "复制模版", description = "复制一个模版")
    @ApiOperationSupport(order = 15, author = "nacoyer")
    public CommonResult<Boolean> copy(@Validated @RequestBody TemplateRequest template) {
        return CommonResult.success(templateService.copy(template));
    }

    @PutMapping("/modify")
    @Operation(summary = "更新模版", description = "根据 ID 更新模版")
    @ApiOperationSupport(order = 16, author = "nacoyer")
    public CommonResult<Boolean> modify(@Validated @RequestBody TemplateUpdateRequest template) {
        return CommonResult.success(templateService.modify(template));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除模版", description = "根据 ID 删除模版")
    @ApiOperationSupport(order = 17, author = "nacoyer")
    public CommonResult<Boolean> delete(@Parameter(name = "模版 ID") @RequestParam("id") Long id) {
        return CommonResult.success(templateService.delete(id));
    }

    @PostMapping("/verifyHasDownloaded")
    @Operation(summary = "校验模版是否已经下载过", description = "校验模版是否已经下载过")
    @ApiOperationSupport(order = 18, author = "nacoyer")
    public CommonResult<Boolean> verifyHasDownloaded(@Parameter(name = "模版市场 key") @RequestParam("marketKey") String marketKey) {
        return CommonResult.success(templateService.verifyHasDownloaded(marketKey));
    }
}
