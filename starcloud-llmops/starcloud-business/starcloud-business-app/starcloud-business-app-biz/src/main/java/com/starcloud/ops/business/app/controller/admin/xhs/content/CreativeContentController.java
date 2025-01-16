package com.starcloud.ops.business.app.controller.admin.xhs.content;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.api.market.vo.response.AppMarketRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response.CreativePlanBatchRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.*;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.*;
import com.starcloud.ops.business.app.enums.plugin.ProcessMannerEnum;
import com.starcloud.ops.business.app.feign.dto.video.VideoGeneratorConfig;
import com.starcloud.ops.business.app.feign.dto.video.VideoMergeConfig;
import com.starcloud.ops.business.app.feign.dto.video.VideoMergeResult;
import com.starcloud.ops.business.app.model.content.VideoContent;
import com.starcloud.ops.business.app.service.xhs.batch.CreativePlanBatchService;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import com.starcloud.ops.business.app.service.xhs.plan.CreativePlanService;
import com.starcloud.ops.business.app.util.RedSignatureUtil;
import com.starcloud.ops.business.app.util.UserUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@RestController
@RequestMapping("/llm/xhs/content")
@Tag(name = "星河云海-小红书 创作内容", description = "星河云海-小红书 创作内容")
public class CreativeContentController {

    @Resource
    private CreativeContentService creativeContentService;

    @Resource
    private CreativePlanBatchService creativePlanBatchService;

    @Resource
    private CreativePlanService planService;

    @GetMapping("/detail/{uid}")
    @Operation(summary = "创作内容详情")
    public CommonResult<CreativeContentRespVO> detail(@PathVariable("uid") String uid) {
        return CommonResult.success(creativeContentService.detail(uid));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public CommonResult<PageResult<CreativeContentRespVO>> page(@Valid CreativeContentPageReqVO req) {
        PageResult<CreativeContentRespVO> result = creativeContentService.page(req);
        return CommonResult.success(result);
    }

    @GetMapping("/pageSearch")
    @Operation(summary = "分页查询")
    public CommonResult<PageResult<CreativeContentRespVO>> page(@Valid CreativeContentPageReqVOV2 req) {
        PageResult<CreativeContentRespVO> result = creativeContentService.page(req);
        return CommonResult.success(result);
    }

    @PutMapping("/modify")
    @Operation(summary = "修改内容")
    public CommonResult<CreativeContentRespVO> modify(@Valid @RequestBody CreativeContentModifyReqVO request) {
        String uid = creativeContentService.modify(request);
        return CommonResult.success(creativeContentService.detail(uid));
    }

    @PostMapping("/regenerate")
    @Operation(summary = "重新生成")
    public CommonResult<Boolean> regenerate(@Valid @RequestBody CreativeContentRegenerateReqVO request) {
        creativeContentService.regenerate(request);
        return CommonResult.success(true);
    }

    @PostMapping("/retry")
    @Operation(summary = "失败重试")
    public CommonResult<String> retry(@Valid @RequestBody UidRequest request) {
        creativeContentService.retry(request.getUid());
        return CommonResult.success(request.getUid());
    }

    @PostMapping("/cancel")
    @Operation(summary = "取消任务")
    public CommonResult<String> cancel(@Valid @RequestBody UidRequest request) {
        creativeContentService.cancel(request.getUid());
        return CommonResult.success(request.getUid());
    }

    @PostMapping("/like")
    @Operation(summary = "点赞", description = "点赞")
    @ApiOperationSupport(order = 80, author = "nacoyer")
    public CommonResult<String> like(@Validated @RequestBody UidRequest request) {
        creativeContentService.like(request.getUid());
        return CommonResult.success(request.getUid());
    }

    @PostMapping("/unlike")
    @Operation(summary = "取消点赞", description = "取消点赞")
    @ApiOperationSupport(order = 80, author = "nacoyer")
    public CommonResult<String> unlike(@Validated @RequestBody UidRequest request) {
        creativeContentService.unlike(request.getUid());
        return CommonResult.success(request.getUid());
    }

    @GetMapping("/listExample")
    @Operation(summary = "获取示例列表")
    @DataPermission(enable = false)
    @ApiOperationSupport(order = 90, author = "nacoyer")
    public CommonResult<List<CreativeContentRespVO>> listExample(@RequestParam("uidList") List<String> uidList) {
        CreativeContentListReqVO query = new CreativeContentListReqVO();
        query.setUidList(uidList);
        List<CreativeContentRespVO> result = creativeContentService.list(query);
        return CommonResult.success(result);
    }

    @GetMapping("/getExample")
    @Operation(summary = "示例创作内容详情")
    @DataPermission(enable = false)
    @ApiOperationSupport(order = 100, author = "nacoyer")
    public CommonResult<CreativeContentRespVO> exampleDetail(@RequestParam("uid") String uid) {
        return CommonResult.success(creativeContentService.detail(uid));
    }

    @GetMapping("/share")
    @Operation(summary = "分享创作内容")
    @DataPermission(enable = false)
    @ApiOperationSupport(order = 100, author = "nacoyer")
    public CommonResult<CreativeContentRespVO> share(@RequestParam("uid") String uid) {
        CreativeContentRespVO creativeContentRespVO = creativeContentService.detail(uid);
        return CommonResult.success(creativeContentRespVO);
    }

    @GetMapping("/share-list")
    @DataPermission(enable = false)
    @Operation(summary = "分享创作内容列表")
    public CommonResult<ShareContentRespVO> shareList(@RequestParam String batchUid) {
        // 查询计划批次
        CreativePlanBatchRespVO batchResponse = creativePlanBatchService.get(batchUid);
        // 查询应用
        AppMarketRespVO appInformation = planService.getAppInformation(batchResponse.getAppUid(), batchResponse.getSource());
        AppValidate.notNull(appInformation, "计划应用信息不存在！");
        // 查询创作内容
        CreativeContentPageReqVO req = new CreativeContentPageReqVO();
        req.setBatchUid(batchUid);
        req.setPageNo(1);
        req.setPageSize(100);
        PageResult<CreativeContentRespVO> result = creativeContentService.page(req);

        ShareContentRespVO response = new ShareContentRespVO();
        response.setAppName(appInformation.getName());
        response.setPlanUid(batchResponse.getPlanUid());
        response.setBatchUid(batchUid);
        response.setTotalCount(batchResponse.getTotalCount());
        response.setFailureCount(batchResponse.getFailureCount());
        response.setSuccessCount(batchResponse.getSuccessCount());
        response.setStartTime(batchResponse.getStartTime());
        response.setEndTime(batchResponse.getEndTime());
        response.setElapsed(batchResponse.getElapsed());
        response.setStatus(batchResponse.getStatus());
        response.setCreator(UserUtils.getUsername(batchResponse.getCreator()));
        response.setCreateTime(batchResponse.getCreateTime());
        response.setContentList(result.getList());
        return CommonResult.success(response);
    }

    @PostMapping("/qrCode")
    @Operation(summary = "批量生成二维码")
    @DataPermission(enable = false)
    @ApiOperationSupport(order = 100, author = "nacoyer")
    public CommonResult<List<CreativeContentQRCodeRespVO>> batchQrCode(@RequestBody @Validated List<CreativeContentQRCodeReqVO> request) {
        List<CreativeContentQRCodeRespVO> response = creativeContentService.batchQrCode(request);
        return CommonResult.success(response);
    }


    @GetMapping("/shareBuildSignature")
    @Operation(summary = "获取小红书签名")
    @DataPermission(enable = false)
    @ApiOperationSupport(order = 100, author = "nacoyer")
    public CommonResult<Map<String, Object>> shareBuildSignature() {

        return CommonResult.success(RedSignatureUtil.buildSignatureApi());
    }


    @PostMapping("batchExecute")
    @Operation(summary = "批量执行", description = "批量执行")
    @ApiOperationSupport(order = 110, author = "nacoyer")
    public CommonResult<List<CreativeContentExecuteRespVO>> batchExecute(@Validated @RequestBody List<String> request) {
        CreativeContentListReqVO query = new CreativeContentListReqVO();
        query.setUidList(request);
        List<CreativeContentRespVO> list = creativeContentService.list(query);
        List<CreativeContentExecuteReqVO> collect = CollectionUtil.emptyIfNull(list).stream()
                .map(item -> {
                    CreativeContentExecuteReqVO requestItem = new CreativeContentExecuteReqVO();
                    requestItem.setUid(item.getUid());
                    requestItem.setForce(Boolean.TRUE);
                    requestItem.setTenantId(item.getTenantId());
                    requestItem.setBatchUid(item.getBatchUid());
                    requestItem.setPlanUid(item.getPlanUid());
                    return requestItem;
                })
                .collect(Collectors.toList());
        List<CreativeContentExecuteRespVO> response = creativeContentService.batchExecute(collect);
        return CommonResult.success(response);
    }

    @PostMapping("/riskword")
    @Operation(summary = "敏感词检测", description = "敏感词检测")
    public CommonResult<CreativeContentRiskRespVO> risk(@Valid @RequestBody CreativeContentRiskReqVO reqVO) {
        CreativeContentRiskRespVO respVO = creativeContentService.risk(reqVO);
        return CommonResult.success(respVO);
    }


    @PostMapping("/riskReplace")
    @Operation(summary = "敏感词替换", description = "敏感词替换")
    public CommonResult<RiskReplaceRespVO> riskReplace(@Valid @RequestBody RiskReplaceReqVO reqVO) {
        return CommonResult.success(reqVO.riskReplace());
    }

    @GetMapping("/metadata")
    @Operation(summary = "元数据", description = "元数据")
    public CommonResult<Map<String, Object>> metadata() {
        Map<String, Object> metadata = Maps.newHashMap();
        metadata.put("processManner", ProcessMannerEnum.options());
        return CommonResult.success(metadata);
    }

    @PostMapping("/video/quick")
    @Operation(summary = "保存视频快捷配置", description = "保存视频快捷配置")
    public CommonResult<Boolean> quickConfiguration(@Valid @RequestBody VideoConfigReqVO reqVO) {
        creativeContentService.saveVideoConfig(reqVO);
        return CommonResult.success(true);
    }

    @PostMapping("/video/generate")
    @Operation(summary = "生成视频", description = "生成视频")
    public CommonResult<VideoGeneratorConfig> generateVideo(@Valid @RequestBody VideoConfigReqVO reqVO) {
        return CommonResult.success(creativeContentService.generateVideo(reqVO));
    }

    @PostMapping("/video/result")
    @Operation(summary = "生成视频结果", description = "生成视频结果")
    public CommonResult<VideoContent> generateResult(@Valid @RequestBody VideoResultReqVO resultReqVO) {
        return CommonResult.success(creativeContentService.videoResult(resultReqVO));
    }

    @PostMapping("/video/merge")
    @Operation(summary = "视频合并", description = "视频合并")
    public CommonResult<VideoMergeResult> generateResult(@Valid @RequestBody VideoMergeConfig videoMergeConfig) {
        return CommonResult.success(creativeContentService.videoMerge(videoMergeConfig));
    }

    @GetMapping("/resource/{uid}")
    @Operation(summary = "获取创作内容资源配置")
    public CommonResult<CreativeContentResourceRespVO> getResource(@PathVariable("uid") String uid) {
        return CommonResult.success(creativeContentService.getResource(uid));
    }
}
