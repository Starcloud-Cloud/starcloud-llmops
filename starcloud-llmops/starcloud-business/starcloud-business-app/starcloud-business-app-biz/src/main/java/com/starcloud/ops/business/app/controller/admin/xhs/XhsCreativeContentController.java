package com.starcloud.ops.business.app.controller.admin.xhs;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentBusinessReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentModifyReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentPageReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsCreativeContentResp;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeContentService;
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
public class XhsCreativeContentController {

    @Resource
    private XhsCreativeContentService creativeContentService;

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public CommonResult<PageResult<XhsCreativeContentResp>> page(@Valid XhsCreativeContentPageReq req) {
        PageResult<XhsCreativeContentResp> result = creativeContentService.page(req);
        return CommonResult.success(result);
    }

    @GetMapping("/newPage")
    @Operation(summary = "分页查询")
    public CommonResult<com.starcloud.ops.business.app.controller.admin.xhs.vo.response.PageResult<XhsCreativeContentResp>> newPage(@Valid XhsCreativeContentPageReq req) {
        com.starcloud.ops.business.app.controller.admin.xhs.vo.response.PageResult<XhsCreativeContentResp> result = creativeContentService.newPage(req);
        return CommonResult.success(result);
    }

    @GetMapping("/detail/{businessUid}")
    @Operation(summary = "创作内容详情")
    public CommonResult<XhsCreativeContentResp> detail(
            @PathVariable("businessUid") String businessUid) {
        return CommonResult.success(creativeContentService.detail(businessUid));
    }

    @PutMapping("/modify")
    @Operation(summary = "修改内容")
    public CommonResult<XhsCreativeContentResp> modify(@Valid @RequestBody XhsCreativeContentModifyReq modifyReq) {
        XhsCreativeContentResp detail = creativeContentService.modify(modifyReq);
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
    public CommonResult<XhsCreativeContentResp> retry(
            @PathVariable("businessUid") String businessUid) {
        return CommonResult.success(creativeContentService.retry(businessUid));
    }

    @PostMapping("/like")
    @Operation(summary = "点赞", description = "点赞")
    @ApiOperationSupport(order = 80, author = "nacoyer")
    public CommonResult<String> like(@Validated @RequestBody XhsCreativeContentBusinessReq request) {
        creativeContentService.like(request.getBusinessUid());
        return CommonResult.success("点赞成功");
    }

    @PostMapping("/unlike")
    @Operation(summary = "取消点赞", description = "取消点赞")
    @ApiOperationSupport(order = 80, author = "nacoyer")
    public CommonResult<String> unlike(@Validated @RequestBody XhsCreativeContentBusinessReq request) {
        creativeContentService.unlike(request.getBusinessUid());
        return CommonResult.success("取消点赞成功");
    }

}
