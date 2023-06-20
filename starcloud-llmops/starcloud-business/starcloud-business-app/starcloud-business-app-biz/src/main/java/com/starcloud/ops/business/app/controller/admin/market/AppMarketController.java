package com.starcloud.ops.business.app.controller.admin.market;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.market.dto.AppMarketDTO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketUpdateReqVO;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import io.swagger.v3.oas.annotations.Operation;
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
    public CommonResult<PageResp<AppMarketDTO>> page(@Validated AppMarketPageQuery query) {
        return CommonResult.success(appMarketService.page(query));
    }

    @GetMapping("/get")
    @Operation(summary = "根据 UID 和版本号 获得应用详情", description = "根据 UID 获取应用详情")
    @ApiOperationSupport(order = 11, author = "nacoyer")
    public CommonResult<AppMarketDTO> get(@RequestParam("uid") String uid, @RequestParam("version") Integer version) {
        return CommonResult.success(appMarketService.getByUid(uid, version));
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

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用市场模版", description = "根据 UID 删除应用市场应用")
    @ApiOperationSupport(order = 15, author = "nacoyer")
    public CommonResult<Boolean> delete(@RequestParam("uid") String uid, @RequestParam("version") Integer version) {
        appMarketService.deleteByUid(uid, version);
        return CommonResult.success(Boolean.TRUE);
    }

}
