package com.starcloud.ops.business.app.controller.admin.market;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.favorite.vo.response.AppFavoriteRespVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppInstallReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketAuditReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageAdminQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketPageQuery;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketReqVO;
import com.starcloud.ops.business.app.api.market.vo.request.AppMarketUpdateReqVO;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.api.operate.request.AppOperateReqVO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.service.market.AppMarketService;
import com.starcloud.ops.framework.common.api.dto.PageQuery;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/pageAdmin")
    @Operation(summary = "后台使用-分页查询应用市应用列表", description = "后台使用-分页查询应用市应用列表")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<PageResp<AppMarketRespVO>> pageAdmin(@Validated AppMarketPageAdminQuery query) {
        return CommonResult.success(appMarketService.pageAdmin(query));
    }

    @GetMapping("/historyPublished")
    @Operation(summary = "历史发布记录", description = "历史发布记录")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<PageResp<AppMarketRespVO>> historyPublished(@RequestParam("uid") String uid,
                                                                    @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return CommonResult.success(appMarketService.historyPublished(uid, PageQuery.of(pageNo, pageSize)));
    }

    @GetMapping("/getByUid/{uid}")
    @Operation(summary = "根据 UID 和版本号 获得应用详情", description = "根据 UID 获取应用详情")
    @ApiOperationSupport(order = 12, author = "nacoyer")
    public CommonResult<AppMarketRespVO> getByUid(@PathVariable("uid") String uid) {
        return CommonResult.success(appMarketService.get(uid, null));
    }

    @GetMapping("/getByUidAndVersion/{uid}/{version}")
    @Operation(summary = "根据 UID 和版本号 获得应用详情", description = "根据 UID 获取应用详情")
    @ApiOperationSupport(order = 15, author = "nacoyer")
    public CommonResult<AppMarketRespVO> getByUidAndVersion(@PathVariable("uid") String uid, @PathVariable("version") Integer version) {
        return CommonResult.success(appMarketService.get(uid, version));
    }

    @GetMapping("/listFavorite")
    @Operation(summary = "获取当前用户的收藏应用列表", description = "获取当前用户的收藏应用列表")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<List<AppFavoriteRespVO>> listFavorite() {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            return CommonResult.error(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        return CommonResult.success(appMarketService.listFavorite(Long.toString(loginUserId)));
    }

    @GetMapping("/getFavorite/{uid}")
    @Operation(summary = "根据 UID 获取收藏应用详情", description = "根据 UID 获取收藏应用详情")
    @ApiOperationSupport(order = 25, author = "nacoyer")
    public CommonResult<AppFavoriteRespVO> getFavorite(@PathVariable("uid") String uid) {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            return CommonResult.error(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        return CommonResult.success(appMarketService.getFavoriteApp(Long.toString(loginUserId), uid));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用市场应用", description = "创建应用市场应用")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<Boolean> create(@Validated @RequestBody AppMarketReqVO request) {
        appMarketService.create(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PutMapping("/modify")
    @Operation(summary = "更新应用市场应用", description = "根据 UID 更新应用市场应用")
    @ApiOperationSupport(order = 35, author = "nacoyer")
    public CommonResult<Boolean> modify(@Validated @RequestBody AppMarketUpdateReqVO request) {
        appMarketService.modify(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @DeleteMapping("/delete/{uid}/{version}")
    @Operation(summary = "删除应用市场模版", description = "根据 UID 删除应用市场应用")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<Boolean> delete(@PathVariable("uid") String uid, @PathVariable("version") Integer version) {
        appMarketService.deleteByUidAndVersion(uid, version);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/install")
    @Operation(summary = "安装应用市场应用", description = "安装应用市场应用")
    @ApiOperationSupport(order = 45, author = "nacoyer")
    public CommonResult<Boolean> install(@Validated @RequestBody AppInstallReqVO request) {
        appMarketService.install(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/audit")
    @Operation(summary = "审核应用市场应用", description = "审核应用市场应用")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public CommonResult<Boolean> audit(@Validated @RequestBody AppMarketAuditReqVO request) {
        appMarketService.audit(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/cancelAudit")
    @Operation(summary = "取消审核", description = "审核应用市场应用")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public CommonResult<Boolean> cancelAudit(@Validated @RequestBody UidRequest request) {
        appMarketService.cancelAudit(request.getMarketUid());
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/operate")
    @Operation(summary = "操作应用市场应用", description = "操作应用市场应用")
    @ApiOperationSupport(order = 55, author = "nacoyer")
    public CommonResult<Boolean> operate(@Validated @RequestBody AppOperateReqVO request) {
        appMarketService.operate(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/favorite")
    @Operation(summary = "收藏应用", description = "收藏应用")
    @ApiOperationSupport(order = 60, author = "nacoyer")
    public CommonResult<Boolean> favorite(@Validated @RequestBody UidRequest request) {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            return CommonResult.error(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        appMarketService.favorite(Long.toString(loginUserId), request.getMarketUid());
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/cancelFavorite")
    @Operation(summary = "取消收藏应用", description = "取消收藏应用")
    @ApiOperationSupport(order = 65, author = "nacoyer")
    public CommonResult<Boolean> cancelFavorite(@Validated @RequestBody UidRequest request) {
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        if (loginUserId == null) {
            return CommonResult.error(ErrorCodeConstants.USER_MAY_NOT_LOGIN);
        }
        appMarketService.cancelFavorite(Long.toString(loginUserId), request.getMarketUid());
        return CommonResult.success(Boolean.TRUE);
    }

}
