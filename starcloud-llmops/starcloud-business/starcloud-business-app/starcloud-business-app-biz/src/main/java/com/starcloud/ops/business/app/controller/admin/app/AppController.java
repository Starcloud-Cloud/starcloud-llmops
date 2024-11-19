package com.starcloud.ops.business.app.controller.admin.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.operatelog.core.annotations.OperateLog;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.app.vo.request.AppPageQuery;
import com.starcloud.ops.business.app.api.app.vo.request.AppUpdateReqVO;
import com.starcloud.ops.business.app.api.app.vo.request.config.VariableReqVO;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.category.vo.AppCategoryVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppModifyExtendReqVO;
import com.starcloud.ops.business.app.service.app.AppManager;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.user.api.dept.DeptPermissionApi;
import com.starcloud.ops.business.user.enums.dept.PartEnum;
import com.starcloud.ops.business.user.pojo.PermissionResult;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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

    @Resource
    private AppManager appManager;


    @Resource
    private DeptPermissionApi deptPermissionApi;

    @GetMapping("/categories")
    @Operation(summary = "查询应用类别列表", description = "查询应用类别列表")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<List<AppCategoryVO>> categories(@RequestParam(value = "isRoot", required = false, defaultValue = "true") Boolean isRoot) {
        return CommonResult.success(appService.categoryList(isRoot));
    }

    @GetMapping("/categoryTree")
    @Operation(summary = "查询应用类别树", description = "查询应用类别树")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<List<AppCategoryVO>> categoryTree() {
        return CommonResult.success(appService.categoryTree());
    }

    @GetMapping("/metadata")
    @Operation(summary = "查询元数据", description = "查询元数据")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<Map<String, List<Option>>> metadata() {
        return CommonResult.success(appService.metadata());
    }

    @GetMapping("/recommends")
    @Operation(summary = "查询推荐的应用列表", description = "查询推荐的应用列表")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<List<AppRespVO>> recommends(@RequestParam(value = "model", required = false) String model) {
        return CommonResult.success(appService.listRecommendedApps(model));
    }

    @GetMapping("/getRecommendApp/{uid}")
    @Operation(summary = "根据推荐应用唯一标识获得推荐应用详情", description = "根据 推荐应用唯一标识 获得推荐应用详情")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public CommonResult<AppRespVO> getRecommendApp(@Parameter(name = "uid", description = "推荐应用唯一标识")
                                                   @PathVariable("uid") String uid) {
        return CommonResult.success(appService.getRecommendApp(uid));
    }

    @GetMapping("/stepList/{type}")
    @Operation(summary = "获取步骤列表", description = "获取步骤列表")
    @ApiOperationSupport(order = 60, author = "nacoyer")
    public CommonResult<List<WorkflowStepWrapperRespVO>> stepList(@PathVariable("type") String type) {
        return CommonResult.success(appService.stepList(type));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询我的应用列表", description = "分页查询我的应用列表")
    @ApiOperationSupport(order = 70, author = "nacoyer")
    public CommonResult<PageResp<AppRespVO>> page(@Validated AppPageQuery query) {
        return CommonResult.success(appService.page(query));
    }

    @GetMapping("/getMarketUid/{publishUid}")
    @Operation(summary = "分页查询我的应用列表", description = "分页查询我的应用列表")
    @ApiOperationSupport(order = 70, author = "nacoyer")
    public CommonResult<String> getMarketUid(@PathVariable("publishUid") String publishUid) {
        return CommonResult.success(appService.getMarketUid(publishUid));
    }

    @GetMapping("/get/{uid}")
    @Operation(summary = "根据 UID 获得应用", description = "根据 UID 获取应用详情")
    @ApiOperationSupport(order = 80, author = "nacoyer")
    public PermissionResult<AppRespVO> get(@Parameter(name = "uid", description = "应用 UID") @PathVariable("uid") String uid) {
        AppRespVO appRespVO = appService.get(uid);
        return PermissionResult.success(appService.get(uid), deptPermissionApi.getUserMenu(Long.valueOf(appRespVO.getCreator()), PartEnum.app));
    }

    @PostMapping("/create")
    @DataPermission(enable = false)
    @Operation(summary = "创建应用", description = "创建一个新的应用")
    @ApiOperationSupport(order = 90, author = "nacoyer")
    public CommonResult<AppRespVO> create(@Validated @RequestBody AppUpdateReqVO request) {
        return CommonResult.success(appService.create(request));
    }

    @PostMapping("/copy")
    @DataPermission(enable = false)
    @Operation(summary = "复制应用", description = "复制一个应用")
    @ApiOperationSupport(order = 100, author = "nacoyer")
    public CommonResult<AppRespVO> copy(@Validated @RequestBody UidRequest request) {
        return CommonResult.success(appService.copy(request));
    }

    @PutMapping("/modify")
    @DataPermission(enable = false)
    @Operation(summary = "更新应用", description = "根据 UID 更新应用")
    @ApiOperationSupport(order = 110, author = "nacoyer")
    public CommonResult<AppRespVO> modify(@Validated @RequestBody AppModifyExtendReqVO request) {
        return CommonResult.success(appManager.modify(request));
    }

    @DeleteMapping("/delete/{uid}")
    @Operation(summary = "删除应用", description = "根据 UID 删除应用")
    @ApiOperationSupport(order = 120, author = "nacoyer")
    public CommonResult<Boolean> delete(@Parameter(name = "uid", description = "应用 UID") @PathVariable("uid") String uid) {
        appService.delete(uid);
        return CommonResult.success(Boolean.TRUE);
    }

    @PostMapping("/fieldCode")
    @Operation(summary = "生成变量field", description = "生成变量field")
    @OperateLog(enable = false)
    public CommonResult<List<VariableItemRespVO>> generalFieldCode(@RequestBody @Valid VariableReqVO reqVO) {
        return CommonResult.success(appService.generalFieldCode(reqVO));
    }

}
