package com.starcloud.ops.business.app.controller.admin.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.app.vo.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.vo.request.AppPublishReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.AppReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.AppUpdateReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @ApiOperationSupport(order = 6, author = "nacoyer")
    public CommonResult<List<AppCategoryVO>> categories() {
        return CommonResult.success(appService.categories());
    }

    @GetMapping("/languages")
    @Operation(summary = "查询应用语言列表", description = "查询语言列表")
    @ApiOperationSupport(order = 6, author = "nacoyer")
    public CommonResult<List<Option>> languages() {
        return CommonResult.success(appService.languages());
    }

    @GetMapping("/recommends")
    @Operation(summary = "查询推荐的应用列表", description = "查询推荐的应用列表")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<List<AppRespVO>> recommends() {
        return CommonResult.success(appService.listRecommendedApps());
    }

    @GetMapping("/getRecommendApp/{recommend}")
    @Operation(summary = "根据推荐应用唯一标识获得推荐应用详情", description = "根据 推荐应用唯一标识 获得推荐应用详情")
    @ApiOperationSupport(order = 11, author = "nacoyer")
    public CommonResult<AppRespVO> getRecommendApp(@Parameter(name = "recommend", description = "推荐应用唯一标识")
                                                   @PathVariable("recommend") String recommend) {
        return CommonResult.success(appService.getRecommendApp(recommend));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询我的应用列表", description = "分页查询我的应用列表")
    @ApiOperationSupport(order = 12, author = "nacoyer")
    public CommonResult<PageResp<AppRespVO>> page(@Validated AppPageQuery query) {
        return CommonResult.success(appService.page(query));
    }

    @GetMapping("/get/{uid}")
    @Operation(summary = "根据 UID 获得应用", description = "根据 UID 获取应用详情")
    @ApiOperationSupport(order = 14, author = "nacoyer")
    public CommonResult<AppRespVO> get(@Parameter(name = "uid", description = "应用 UID") @PathVariable("uid") String uid) {
        return CommonResult.success(appService.getByUid(uid));
    }

    @PostMapping("/create")
    @Operation(summary = "创建应用", description = "创建一个新的应用")
    @ApiOperationSupport(order = 16, author = "nacoyer")
    public CommonResult<Boolean> create(@Validated @RequestBody AppReqVO request) {
        appService.create(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/copy")
    @Operation(summary = "复制应用", description = "复制一个应用")
    @ApiOperationSupport(order = 18, author = "nacoyer")
    public CommonResult<Boolean> copy(@Validated @RequestBody AppReqVO request) {
        appService.copy(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @PutMapping("/modify")
    @Operation(summary = "更新应用", description = "根据 UID 更新应用")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<Boolean> modify(@Validated @RequestBody AppUpdateReqVO request) {
        appService.modify(request);
        return CommonResult.success(Boolean.TRUE);
    }

    @DeleteMapping("/delete/{uid}")
    @Operation(summary = "删除应用", description = "根据 UID 删除应用")
    @ApiOperationSupport(order = 22, author = "nacoyer")
    public CommonResult<Boolean> delete(@Parameter(name = "uid", description = "应用 UID") @PathVariable("uid") String uid) {
        appService.deleteByUid(uid);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/publish")
    @Operation(summary = "发布应用到应用市场", description = "发布应用到应用市场")
    @ApiOperationSupport(order = 24, author = "nacoyer")
    public CommonResult<Boolean> publish(@Validated @RequestBody AppPublishReqVO request) {
        appService.publish(request);
        return CommonResult.success(Boolean.TRUE);
    }

}
