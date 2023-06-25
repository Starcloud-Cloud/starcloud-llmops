package com.starcloud.ops.business.app.controller.admin.market;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.market.vo.request.*;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 星河云海应用市场管理
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-05
 */
@RestController
@RequestMapping("/llm/app/market")
@Tag(name = "星河云海-应用市场管理", description = "星河云海应用市场管理")
public class AppMarketController {

    @Resource
    private AppMarketService appMarketService;

    @GetMapping("/page")
    @Operation(summary = "分页查询应用市场应用列表", description = "分页查询应用市场应用列表")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<PageResp<AppMarketRespVO>> page(@Validated AppMarketPageQuery query) {
        return CommonResult.success(appMarketService.page(query));
    }

    @GetMapping("/get/{uid}/{version}")
    @Operation(summary = "根据 UID 和版本号 获得应用详情", description = "根据 UID 获取应用详情")
    @ApiOperationSupport(order = 11, author = "nacoyer")
    public CommonResult<AppMarketRespVO> get(@PathVariable("uid") String uid, @PathVariable("version") Integer version) {
        return CommonResult.success(appMarketService.getByUidAndVersion(uid, version));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用市场应用", description = "创建应用市场应用")
    @ApiOperationSupport(order = 12, author = "nacoyer")
    public CommonResult<Boolean> create(@Validated @RequestBody AppMarketReqVO request) {
        appMarketService.create(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PutMapping("/modify")
    @Operation(summary = "更新应用市场应用", description = "根据 UID 更新应用市场应用")
    @ApiOperationSupport(order = 14, author = "nacoyer")
    public CommonResult<Boolean> modify(@Validated @RequestBody AppMarketUpdateReqVO request) {
        appMarketService.modify(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @DeleteMapping("/delete/{uid}/{version}")
    @Operation(summary = "删除应用市场模版", description = "根据 UID 删除应用市场应用")
    @ApiOperationSupport(order = 15, author = "nacoyer")
    public CommonResult<Boolean> delete(@PathVariable("uid") String uid, @PathVariable("version") Integer version) {
        appMarketService.deleteByUidAndVersion(uid, version);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/install")
    @Operation(summary = "安装应用市场应用", description = "安装应用市场应用")
    @ApiOperationSupport(order = 16, author = "nacoyer")
    public CommonResult<Boolean> install(@Validated @RequestBody AppInstallReqVO request) {
        appMarketService.install(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/audit")
    @Operation(summary = "审核应用市场应用", description = "审核应用市场应用")
    @ApiOperationSupport(order = 17, author = "nacoyer")
    public CommonResult<Boolean> audit(@Validated @RequestBody AppMarketAuditReqVO request) {
        appMarketService.audit(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/operate")
    @Operation(summary = "操作应用市场应用", description = "操作应用市场应用")
    @ApiOperationSupport(order = 18, author = "nacoyer")
    public CommonResult<Boolean> operate(@Validated @RequestBody AppOperateReqVO request) {
        appMarketService.operate(request);
        return CommonResult.success(Boolean.TRUE);
    }

}
