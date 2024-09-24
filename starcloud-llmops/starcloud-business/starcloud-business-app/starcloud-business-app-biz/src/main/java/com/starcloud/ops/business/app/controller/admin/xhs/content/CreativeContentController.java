package com.starcloud.ops.business.app.controller.admin.xhs.content;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.base.vo.request.UidRequest;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentListReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentModifyReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.request.CreativeContentRegenerateReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentExecuteRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
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

}
