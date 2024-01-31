package com.starcloud.ops.business.app.controller.admin.xhs.content;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentBusinessReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentModifyReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.request.CreativeContentPageReqVO;
import com.starcloud.ops.business.app.api.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.service.xhs.content.CreativeContentService;
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
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/llm/xhs/content")
@Tag(name = "星河云海-小红书 创作内容", description = "星河云海-小红书 创作内容")
public class CreativeContentController {

    @Resource
    private CreativeContentService creativeContentService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public CommonResult<PageResult<CreativeContentRespVO>> page(@Valid CreativeContentPageReqVO req) {
        PageResult<CreativeContentRespVO> result = creativeContentService.page(req);
        return CommonResult.success(result);
    }

    @GetMapping("/newPage")
    @Operation(summary = "分页查询")
    public CommonResult<com.starcloud.ops.business.app.api.xhs.content.vo.response.PageResult<CreativeContentRespVO>> newPage(@Valid CreativeContentPageReqVO req) {
        com.starcloud.ops.business.app.api.xhs.content.vo.response.PageResult<CreativeContentRespVO> result = creativeContentService.newPage(req);
        return CommonResult.success(result);
    }

    @GetMapping("/detail/{businessUid}")
    @Operation(summary = "创作内容详情")
    public CommonResult<CreativeContentRespVO> detail(
            @PathVariable("businessUid") String businessUid) {
        return CommonResult.success(creativeContentService.detail(businessUid));
    }

    @PutMapping("/modify")
    @Operation(summary = "修改内容")
    public CommonResult<CreativeContentRespVO> modify(@Valid @RequestBody CreativeContentModifyReqVO modifyReq) {
        CreativeContentRespVO detail = creativeContentService.modify(modifyReq);
        return CommonResult.success(detail);
    }

    @DeleteMapping("/delete/{businessUid}")
    @Operation(summary = "删除创作内容")
    public CommonResult<Boolean> delete(
            @PathVariable("businessUid") String businessUid) {
        creativeContentService.delete(businessUid);
        return CommonResult.success(true);
    }

    @GetMapping("/retry/{businessUid}")
    @Operation(summary = "执行创作内容")
    public CommonResult<CreativeContentRespVO> retry(
            @PathVariable("businessUid") String businessUid) {
        return CommonResult.success(creativeContentService.retry(businessUid));
    }

    @GetMapping("/failureRetry/{uid}")
    @Operation(summary = "失败重试")
    public CommonResult<Boolean> failureRetry(@PathVariable("uid") String uid) {
        creativeContentService.failureRetry(uid);
        return CommonResult.success(true);
    }

    @PostMapping("/like")
    @Operation(summary = "点赞", description = "点赞")
    @ApiOperationSupport(order = 80, author = "nacoyer")
    public CommonResult<String> like(@Validated @RequestBody CreativeContentBusinessReqVO request) {
        creativeContentService.like(request.getBusinessUid());
        return CommonResult.success("点赞成功");
    }

    @PostMapping("/unlike")
    @Operation(summary = "取消点赞", description = "取消点赞")
    @ApiOperationSupport(order = 80, author = "nacoyer")
    public CommonResult<String> unlike(@Validated @RequestBody CreativeContentBusinessReqVO request) {
        creativeContentService.unlike(request.getBusinessUid());
        return CommonResult.success("取消点赞成功");
    }

}
