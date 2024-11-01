package com.starcloud.ops.business.app.controller.admin.xhs.provider;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.base.vo.request.BatchUidRequest;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.*;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@RestController
@RequestMapping("/creative/provider/coze")
@Tag(name = "coze接口提供", description = "")
public class CozeApiController {

    @Resource
    private CreativePlanService creativePlanService;

    @Resource
    private CreativePlanExecuteManager creativePlanExecuteManager;


    @GetMapping("/appList")
    @Operation(summary = "获取可执行的应用列表", description = "获取可执行的应用列表")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public CommonResult<List<String>> appList() {

        List<String> result = new ArrayList();

        result.add("sd");
        result.add("333");

        return CommonResult.success(result);
    }


    @GetMapping("/planPosterList")
    @Operation(summary = "创作计划海报列表", description = "创作计划海报列表")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public CommonResult<List<PosterStyleDTO>> planPosterList(@Validated UidRequest request) {
        return CommonResult.success(creativePlanService.planPosterList(request.getUid()));
    }


    @PostMapping("/run")
    @Operation(summary = "执行创作计划", description = "执行创作计划")
    @ApiOperationSupport(order = 90, author = "nacoyer")
    public CommonResult<PlanExecuteResult> run(@Validated @RequestBody PlanExecuteRequest request) {
        return CommonResult.success(creativePlanExecuteManager.run(request));
    }



    @GetMapping("/getByAppUid")
    @Operation(summary = "获取创作计划详情", description = "获取创作计划详情")
    @ApiOperationSupport(order = 40, author = "nacoyer")
    public CommonResult<CreativePlanRespVO> getByAppUid(@Validated CreativePlanGetQuery query) {
        return CommonResult.success(creativePlanService.getOrCreate(query));
    }


}
