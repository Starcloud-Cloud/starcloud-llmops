package com.starcloud.ops.business.app.controller.admin.xhs.plan;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.base.vo.request.BatchUidRequest;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreateSameAppReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanGetQuery;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanListQuery;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanPageQuery;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.CreativePlanUpgradeReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.model.plan.PlanExecuteRequest;
import com.starcloud.ops.business.app.model.plan.PlanExecuteResult;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanExecuteManager;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.framework.common.api.dto.Option;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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

    @Resource
    private CreativePlanExecuteManager creativePlanExecuteManager;

    @PostMapping(value = "/metadata")
    @Operation(summary = "创作计划元数据", description = "创作计划元数据")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    public CommonResult<Map<String, List<Option>>> metadata() {
        return CommonResult.success(creativePlanService.metadata());
    }

    @PostMapping(value = "/uploadImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传图片", description = "上传图片")
    @ApiOperationSupport(order = 20, author = "nacoyer")
    public CommonResult<UploadImageInfoDTO> upload(@RequestPart("image") MultipartFile image) {
        return CommonResult.success(creativePlanService.uploadImage(image));
    }

    @GetMapping("/getByAppUid")
    @Operation(summary = "获取创作计划详情", description = "获取创作计划详情")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<CreativePlanRespVO> getByAppUid(@Validated CreativePlanGetQuery query) {
        return CommonResult.success(creativePlanService.getOrCreate(query));
    }

    @GetMapping("/list")
    @Operation(summary = "创作计划列表", description = "创作计划列表")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public CommonResult<List<CreativePlanRespVO>> list(@RequestParam(value = "limit", defaultValue = "100") Integer limit) {
        return CommonResult.success(creativePlanService.list(limit));
    }


    @PostMapping("/query")
    @Operation(summary = "创作计划列表", description = "创作计划列表")
    public CommonResult<List<CreativePlanRespVO>> query(@RequestBody CreativePlanListQuery query) {
        return CommonResult.success(creativePlanService.list(query));
    }

    @PostMapping("/createSameApp")
    @Operation(summary = "创建同款应用", description = "创建同款应用")
    @ApiOperationSupport(order = 60, author = "nacoyer")
    public CommonResult<String> create(@Validated @RequestBody CreateSameAppReqVO request) {
        return CommonResult.success(creativePlanService.createSameApp(request));
    }

    @PostMapping("/modify")
    @Operation(summary = "更新创作计划", description = "更新创作计划")
    @ApiOperationSupport(order = 70, author = "nacoyer")
    public CommonResult<CreativePlanRespVO> modify(@Validated @RequestBody CreativePlanModifyReqVO request) {
        return CommonResult.success(creativePlanService.modify(request));
    }

    @PostMapping("/modifyConfig")
    @Operation(summary = "更新创作计划配置", description = "更新创作计划配置")
    public CommonResult<CreativePlanRespVO> modifyConfiguration(@Validated @RequestBody CreativePlanModifyReqVO request) {
        return CommonResult.success(creativePlanService.modifyConfiguration(request));
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
    @ApiOperationSupport(order = 90, author = "nacoyer")
    public CommonResult<PlanExecuteResult> execute(@Validated @RequestBody UidRequest request) {
        PlanExecuteRequest planExecuteRequest = new PlanExecuteRequest();
        planExecuteRequest.setPlanUid(request.getUid());
        planExecuteRequest.setAsync(Boolean.FALSE);
        planExecuteRequest.setMaterialListJson(null);
        planExecuteRequest.setPosterStyleId(null);
        planExecuteRequest.setTotalCount(null);
        return CommonResult.success(creativePlanExecuteManager.execute(planExecuteRequest));
    }



    @PostMapping("/cancel")
    @Operation(summary = "取消创作计划", description = "取消创作计划")
    @ApiOperationSupport(order = 100, author = "nacoyer")
    public CommonResult<Boolean> upgrade(@Validated @RequestBody BatchUidRequest request) {
        creativePlanService.cancel(request.getBatchUid());
        return CommonResult.success(true);
    }

    @PostMapping("/upgrade")
    @Operation(summary = "升级创作计划", description = "升级创作计划")
    @ApiOperationSupport(order = 100, author = "nacoyer")
    public CommonResult<Boolean> upgrade(@Validated @RequestBody CreativePlanUpgradeReqVO request) {
        creativePlanService.upgrade(request);
        return CommonResult.success(true);
    }

}
