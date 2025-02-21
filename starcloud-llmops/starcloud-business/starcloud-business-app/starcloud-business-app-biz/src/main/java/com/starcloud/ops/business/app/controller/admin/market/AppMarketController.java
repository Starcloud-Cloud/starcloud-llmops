package com.starcloud.ops.business.app.controller.admin.market;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketListGroupByCategoryQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketOptionListQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketUpdateReqVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketGroupCategoryRespVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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

    @GetMapping("/listGroupByCategory")
    @Operation(summary = "根据分类分组查询应用市场", description = "根据分类分组查询应用市场")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<List<AppMarketGroupCategoryRespVO>> listGroupByCategory(@Validated AppMarketListGroupByCategoryQuery query) {
        return CommonResult.success(appMarketService.listGroupByCategory(query));
    }

    @GetMapping("/listGroupTemplateByCategory")
    @Operation(summary = "根据分类分组查询应用市场", description = "根据分类分组查询应用市场")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<List<AppMarketGroupCategoryRespVO>> listGroupTemplateByCategory(@Validated AppMarketListGroupByCategoryQuery query) {
        return CommonResult.success(appMarketService.listGroupTemplateByCategory(query));
    }

    @GetMapping("/listMarketAppOption")
    @Operation(summary = "获取应用列表选项", description = "获取应用列表选项")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<List<Option>> listOption(AppMarketOptionListQuery query) {
        return CommonResult.success(appMarketService.listOption(query));
    }

    @GetMapping("/get/{uid}")
    @Operation(summary = "根据 UID 获得应用详情", description = "根据 UID 获取应用详情")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<AppMarketRespVO> get(@PathVariable("uid") String uid) {
        return CommonResult.success(appMarketService.getAndIncreaseView(uid));
    }

    @PostMapping("/modify")
    @Operation(summary = "修改应用市场", description = "修改应用市场")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public CommonResult<Boolean> get(@Validated @RequestBody AppMarketUpdateReqVO request) {
        appMarketService.modify(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @DeleteMapping("/delete/{uid}")
    @PreAuthorize("@ss.hasPermission('app:market:delete')")
    @Operation(summary = "删除应用市场模版", description = "根据 UID 删除应用市场应用")
    @ApiOperationSupport(order = 60, author = "nacoyer")
    public CommonResult<Boolean> delete(@PathVariable("uid") String uid) {
        appMarketService.delete(uid);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/operate")
    @Operation(summary = "操作应用市场应用", description = "操作应用市场应用")
    @ApiOperationSupport(order = 80, author = "nacoyer")
    public CommonResult<Boolean> operate(@Validated @RequestBody AppOperateReqVO request) {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            return CommonResult.error(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        request.setUserId(Long.toString(loginUserId));
        appMarketService.operate(request);
        return CommonResult.success(Boolean.TRUE);
    }

}
