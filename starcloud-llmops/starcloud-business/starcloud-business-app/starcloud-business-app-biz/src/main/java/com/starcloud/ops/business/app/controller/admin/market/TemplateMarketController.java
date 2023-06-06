package com.starcloud.ops.business.app.controller.admin.market;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.market.dto.TemplateMarketDTO;
import com.starcloud.ops.business.app.api.market.request.TemplateMarketPageQuery;
import com.starcloud.ops.business.app.api.market.request.TemplateMarketRequest;
import com.starcloud.ops.business.app.api.market.request.TemplateMarketUpdateRequest;
import com.starcloud.ops.business.app.service.market.TemplateMarketService;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@RestController
@RequestMapping("/llm/app/market")
@Tag(name = "星河云海-模版市场管理", description = "星河云海模版市场管理")
public class TemplateMarketController {

    @Resource
    private TemplateMarketService templateMarketService;


    @GetMapping("/page")
    @Operation(summary = "分页查询模版模版市场模版列表", description = "分页查询模版市场模版列表")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<PageResp<TemplateMarketDTO>> page(@Validated TemplateMarketPageQuery query) {
        return CommonResult.success(templateMarketService.page(query));
    }

    @GetMapping("/get")
    @Operation(summary = "获得模版", description = "根据 UID 获取模版详情")
    @ApiOperationSupport(order = 11, author = "nacoyer")
    public CommonResult<TemplateMarketDTO> get(@Parameter(name = "模版市场模版 UID") @RequestParam("uid") String uid) {
        return CommonResult.success(templateMarketService.getByUid(uid));
    }

    @PostMapping("/create")
    @Operation(summary = "创建模版市场模版", description = "创建模版市场模版")
    @ApiOperationSupport(order = 12, author = "nacoyer")
    public CommonResult<Boolean> create(@Validated @RequestBody TemplateMarketRequest template) {
        return CommonResult.success(templateMarketService.create(template));
    }

    @PutMapping("/modify")
    @Operation(summary = "更新模版市场模版", description = "根据 UID 更新模版市场模版")
    @ApiOperationSupport(order = 14, author = "nacoyer")
    public CommonResult<Boolean> modify(@Validated @RequestBody TemplateMarketUpdateRequest template) {
        return CommonResult.success(templateMarketService.modify(template));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除模版市场模版", description = "根据 UID 删除模版市场模版")
    @ApiOperationSupport(order = 15, author = "nacoyer")
    public CommonResult<Boolean> delete(@Parameter(name = "模版市场模版 UID") @RequestParam("uid") String uid) {
        return CommonResult.success(templateMarketService.deleteByUid(uid));
    }

}
