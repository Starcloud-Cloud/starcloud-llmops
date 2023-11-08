package com.starcloud.ops.business.app.controller.admin.xhs;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentModifyReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.request.XhsCreativeContentPageReq;
import com.starcloud.ops.business.app.controller.admin.xhs.vo.response.XhsCreativeContentResp;
import com.starcloud.ops.business.app.service.xhs.XhsCreativeContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/detail/{businessUid}")
    @Operation(summary = "创作内容详情")
    public CommonResult<XhsCreativeContentResp> detail(
            @PathVariable("businessUid")String businessUid) {
        return CommonResult.success(creativeContentService.detail(businessUid));
    }

    @GetMapping("/modify")
    @Operation(summary = "修改内容")
    public CommonResult<XhsCreativeContentResp> modify(@Valid @RequestBody XhsCreativeContentModifyReq modifyReq) {
        XhsCreativeContentResp detail = creativeContentService.modify(modifyReq);
        return CommonResult.success(detail);
    }
}
