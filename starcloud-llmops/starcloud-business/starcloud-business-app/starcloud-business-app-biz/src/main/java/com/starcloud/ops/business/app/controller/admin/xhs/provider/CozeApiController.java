package com.starcloud.ops.business.app.controller.admin.xhs.provider;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.service.dict.DictDataService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.collect.Lists;
import com.starcloud.ops.business.app.api.base.vo.request.BatchUidRequest;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.image.dto.UploadImageInfoDTO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.request.*;
import com.starcloud.ops.business.app.controller.admin.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.model.plan.PlanExecuteRequest;
import com.starcloud.ops.business.app.model.plan.PlanExecuteResult;
import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanExecuteManager;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.framework.common.api.dto.Option;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Arrays;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Slf4j
@RestController
@RequestMapping("/creative/provider/coze")
@Tag(name = "coze接口提供", description = "")
public class CozeApiController {

    @Resource
    private CreativePlanService creativePlanService;

    @Resource
    private CreativePlanExecuteManager creativePlanExecuteManager;

    @Resource
    private DictDataService dictDataService;

    @Resource
    private CreativeContentService creativeContentService;


    @GetMapping("/appList")
    @Operation(summary = "获取可执行的应用列表", description = "获取可执行的应用列表")
    @ApiOperationSupport(order = 50, author = "nacoyer")
    public CommonResult<List<Object>> appList() {

        DictDataDO dictDataDO = dictDataService.parseDictData("provider_coze_appList", "appList" );

        return CommonResult.success(JSONUtil.toList(dictDataDO.getRemark(), Object.class));
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



    @GetMapping("/detail")
    @Operation(summary = "应用批次执行详情")
    public CommonResult<List<CreativeContentRespVO>> page(@Valid CreativeContentPageReqVO req) {

        req.setPageSize(30);
        req.setPageNo(1);

        PageResult<CreativeContentRespVO> result = creativeContentService.page(req);

        return CommonResult.success(result.getList());
    }



}
