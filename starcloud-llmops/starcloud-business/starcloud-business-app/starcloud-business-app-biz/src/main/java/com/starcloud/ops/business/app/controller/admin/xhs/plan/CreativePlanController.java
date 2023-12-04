package com.starcloud.ops.business.app.controller.admin.xhs.plan;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.request.CreativePlanStatusReqVO;
import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@RestController
@RequestMapping("/llm/creative/plan")
@Tag(name = "星河云海-创作计划", description = "星河云海创作计划管理")
public class CreativePlanController {

    @Resource
    private CreativePlanService creativePlanService;

    @GetMapping("/get/{uid}")
    @Operation(summary = "获取创作计划详情", description = "获取创作计划详情")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<CreativePlanRespVO> get(@PathVariable String uid) {
        return CommonResult.success(creativePlanService.get(uid));
    }

    @GetMapping("/listTemplates")
    @Operation(summary = "获取默认模板列表", description = "获取默认模板列表")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<List<CreativePlanRespVO>> listTemplates() {
        return CommonResult.success(creativePlanService.listTemplates());
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询创作计划列表", description = "分页查询创作计划列表")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<PageResp<CreativePlanRespVO>> page(CreativePlanPageQuery query) {
        return CommonResult.success(creativePlanService.page(query));
    }

    @PostMapping("/create")
    @Operation(summary = "创建创作计划", description = "创建创作计划")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public CommonResult<String> create(@Validated @RequestBody CreativePlanReqVO request) {
        return CommonResult.success(creativePlanService.create(request));
    }

    @PostMapping("/copy")
    @Operation(summary = "复制创作计划", description = "复制创作计划")
    @ApiOperationSupport(order = 60, author = "nacoyer")
    public CommonResult<String> copy(@Validated @RequestBody UidRequest request) {
        return CommonResult.success(creativePlanService.copy(request));
    }

    @PostMapping("/modify")
    @Operation(summary = "更新创作计划", description = "更新创作计划")
    @ApiOperationSupport(order = 70, author = "nacoyer")
    public CommonResult<String> modify(@Validated @RequestBody CreativePlanModifyReqVO request) {
        return CommonResult.success(creativePlanService.modify(request));
    }

    @PostMapping("/status")
    @Operation(summary = "更新创作计划状态", description = "更新创作计划状态")
    @ApiOperationSupport(order = 70, author = "nacoyer")
    public CommonResult<String> modify(@Validated @RequestBody CreativePlanStatusReqVO request) {
        if (!CreativePlanStatusEnum.PAUSE.name().equals(request.getStatus()) &&
                !CreativePlanStatusEnum.RUNNING.name().equals(request.getStatus()) &&
                !CreativePlanStatusEnum.CANCELED.name().equals(request.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.CREATIVE_PLAN_STATUS_NOT_SUPPORTED);
        }
        creativePlanService.updateStatus(request.getUid(), request.getStatus());
        return CommonResult.success("创作计划更新成功");
    }

    @DeleteMapping("/delete/{uid}")
    @Operation(summary = "删除创作计划", description = "删除创作计划")
    @ApiOperationSupport(order = 80, author = "nacoyer")
    public CommonResult<Boolean> delete(@PathVariable String uid) {
        creativePlanService.delete(uid);
        return CommonResult.success(true);
    }

    @PostMapping("/execute")
    @Operation(summary = "执行创作计划", description = "执行创作计划")
    @ApiOperationSupport(order = 80, author = "nacoyer")
    public CommonResult<Boolean> execute(@Validated @RequestBody UidRequest request) {
        creativePlanService.execute(request.getUid());
        return CommonResult.success(true);
    }
}
