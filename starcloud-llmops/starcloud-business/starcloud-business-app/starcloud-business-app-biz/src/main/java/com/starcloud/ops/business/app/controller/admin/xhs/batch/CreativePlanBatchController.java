package com.starcloud.ops.business.app.controller.admin.xhs.batch;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.request.CreativePlanBatchPageReqVO;
import com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response.CreativePlanBatchRespVO;
import com.starcloud.ops.business.app.service.xhs.batch.CreativePlanBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/llm/creative/batch")
@Tag(name = "星河云海-创作计划批次", description = "创作计划批次")
public class CreativePlanBatchController {

    @Resource
    private CreativePlanBatchService batchService;

    @GetMapping("/page")
    @Operation(summary = "分页查询创作计划批次列表", description = "分页查询创作计划批次列表")
    public CommonResult<PageResult<CreativePlanBatchRespVO>> page(CreativePlanBatchPageReqVO pageReqVO) {
        return CommonResult.success(batchService.page(pageReqVO));
    }
}
