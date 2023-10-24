package com.starcloud.ops.business.app.controller.admin.favorite;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.favorite.vo.query.AppFavoriteListReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.query.AppFavoritePageReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.request.AppFavoriteCancelReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.request.AppFavoriteCreateReqVO;
import com.starcloud.ops.business.app.api.favorite.vo.response.AppFavoriteRespVO;
import com.starcloud.ops.business.app.service.favorite.AppFavoriteService;
import com.starcloud.ops.business.app.service.spell.SpellService;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-10-24
 */
@RestController
@RequestMapping("/llm/app/favorite")
@Tag(name = "星河云海-应用收藏管理", description = "星河云海应用收藏管理")
public class AppFavoriteController {

    @Resource
    private AppFavoriteService appFavoriteService;

    @PostMapping("/getMarketInfo")
    @Operation(summary = "根据收藏 UID 获取收藏应用详情", description = "根据收藏 UID 获取收藏应用详情")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<AppFavoriteRespVO> getMarketInfo(@Validated @RequestBody UidRequest request) {
        return CommonResult.success(appFavoriteService.getMarketInfo(request.getUid()));
    }

    @PostMapping("/list")
    @Operation(summary = "获取当前用户的收藏应用列表", description = "获取当前用户的收藏应用列表")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<List<AppFavoriteRespVO>> list(@Validated @RequestBody(required = false) AppFavoriteListReqVO query) {
        return CommonResult.success(appFavoriteService.list(query));
    }

    @PostMapping("/page")
    @Operation(summary = "获取当前用户的收藏应用分页列表", description = "获取当前用户的收藏应用分页列表")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<PageResp<AppFavoriteRespVO>> page(@Validated @RequestBody(required = false) AppFavoritePageReqVO query) {
        return CommonResult.success(appFavoriteService.page(query));
    }

    @PostMapping("/collect")
    @Operation(summary = "收藏应用", description = "收藏应用")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<Boolean> favorite(@Validated @RequestBody AppFavoriteCreateReqVO request) {
        appFavoriteService.create(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消收藏应用", description = "取消收藏应用")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public CommonResult<Boolean> cancel(@Validated @RequestBody AppFavoriteCancelReqVO request) {
        appFavoriteService.cancel(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @Resource
    private SpellService spellService;

    @GetMapping("/test")
    public CommonResult<String> test(@RequestParam("text") String text) {
        return CommonResult.success(spellService.getSpell(text));
    }

}
