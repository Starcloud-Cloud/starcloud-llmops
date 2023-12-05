package com.starcloud.ops.business.app.controller.admin.xhs.scheme;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeImageTemplateDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeSchemeExampleDTO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeListReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemePageReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.request.CreativeSchemeReqVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeListOptionRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.scheme.vo.CreativeSchemeSseReqVO;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.service.xhs.scheme.CreativeSchemeService;
import com.starcloud.ops.framework.common.api.util.SseEmitterUtil;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@RestController
@RequestMapping("/llm/creative/scheme")
@Tag(name = "星河云海-创作方案", description = "星河云海创作计划管理")
public class CreativeSchemeController {

    @Resource
    private CreativeSchemeService creativeSchemeService;

    @GetMapping("/metadata")
    @Operation(summary = "获取创作方案元数据", description = "获取创作方案元数据")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<Map<String, Object>> metadata() {
        return CommonResult.success(creativeSchemeService.metadata());
    }

    @GetMapping("/templates")
    @Operation(summary = "获取图片模板列表", description = "获取图片模板列表")
    @ApiOperationSupport(order = 30, author = "nacoyer")
    public CommonResult<List<CreativeImageTemplateDTO>> templates() {
        return CommonResult.success(creativeSchemeService.templates());
    }

    @GetMapping("/get/{uid}")
    @Operation(summary = "获取创作方案详情", description = "获取创作方案详情")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<CreativeSchemeRespVO> get(@PathVariable String uid) {
        return CommonResult.success(creativeSchemeService.get(uid));
    }

    @GetMapping("/list")
    @DataPermission(enable = false)
    @Operation(summary = "查询创作方案列表", description = "查询创作方案列表")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public CommonResult<List<CreativeSchemeRespVO>> list(CreativeSchemeListReqVO query) {
        return CommonResult.success(creativeSchemeService.list(query));
    }

    @GetMapping("/listOption")
    @DataPermission(enable = false)
    @Operation(summary = "查询创作方案列表Option", description = "查询创作方案列表Option")
    @ApiOperationSupport(order = 60, author = "nacoyer")
    public CommonResult<List<CreativeSchemeListOptionRespVO>> listOption(CreativeSchemeListReqVO query) {
        return CommonResult.success(creativeSchemeService.listOption(query));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询创作方案列表", description = "分页查询创作方案列表")
    @ApiOperationSupport(order = 70, author = "nacoyer")
    public CommonResult<PageResult<CreativeSchemeRespVO>> page(CreativeSchemePageReqVO query) {
        return CommonResult.success(creativeSchemeService.page(query));
    }

    @PostMapping("/create")
    @Operation(summary = "创建创作方案", description = "创建创作方案")
    @ApiOperationSupport(order = 80, author = "nacoyer")
    public CommonResult<String> create(@Validated @RequestBody CreativeSchemeReqVO request) {
        creativeSchemeService.create(request);
        return CommonResult.success("创作方案创建成功");
    }

    @PostMapping("/copy")
    @Operation(summary = "复制创作方案", description = "复制创作方案")
    @ApiOperationSupport(order = 90, author = "nacoyer")
    public CommonResult<String> copy(@Validated @RequestBody UidRequest request) {
        creativeSchemeService.copy(request);
        return CommonResult.success("创作方案复制成功");
    }

    @PostMapping("/modify")
    @Operation(summary = "更新创作方案", description = "更新创作方案")
    @ApiOperationSupport(order = 100, author = "nacoyer")
    public CommonResult<String> modify(@Validated @RequestBody CreativeSchemeModifyReqVO request) {
        creativeSchemeService.modify(request);
        return CommonResult.success("创作方案更新成功");
    }

    @DeleteMapping("/delete/{uid}")
    @Operation(summary = "删除创作方案", description = "删除创作方案")
    @ApiOperationSupport(order = 110, author = "nacoyer")
    public CommonResult<Boolean> delete(@PathVariable String uid) {
        creativeSchemeService.delete(uid);
        return CommonResult.success(true);
    }

    @PostMapping(value = "/summary")
    @Operation(summary = "小红书需求生成")
    @ApiOperationSupport(order = 120, author = "nacoyer")
    public SseEmitter summary(@Validated @RequestBody CreativeSchemeSseReqVO executeRequest, HttpServletResponse httpServletResponse) {
        // 设置响应头
        httpServletResponse.setHeader(AppConstants.CACHE_CONTROL, AppConstants.CACHE_CONTROL_VALUE);
        httpServletResponse.setHeader(AppConstants.X_ACCEL_BUFFERING, AppConstants.X_ACCEL_BUFFERING_VALUE);
        // 设置 SSE
        SseEmitter emitter = SseEmitterUtil.ofSseEmitterExecutor(5 * 60000L, "xhs demand");
        executeRequest.setSseEmitter(emitter);
        // 异步执行应用
        creativeSchemeService.summary(executeRequest);
        return emitter;
    }

    @PostMapping(value = "/example")
    @Operation(summary = "小红书文案测试生成")
    @ApiOperationSupport(order = 110, author = "nacoyer")
    public CommonResult<List<CreativeSchemeExampleDTO>> example(@Validated @RequestBody CreativeSchemeReqVO executeRequest) {
        return CommonResult.success(creativeSchemeService.example(executeRequest));
    }

}
