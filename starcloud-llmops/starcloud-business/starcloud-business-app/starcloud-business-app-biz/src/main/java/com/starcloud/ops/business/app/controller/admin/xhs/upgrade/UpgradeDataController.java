package com.starcloud.ops.business.app.controller.admin.xhs.upgrade;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@RestController
@RequestMapping("/llm/upgrade")
@Tag(name = "星河云海-数据升级", description = "数据升级")
public class UpgradeDataController {

    @Resource
    private UpgradeDataService upgradeDataService;

    @PostMapping("/v1/app")
    @Operation(summary = "升级应用数据", description = "升级应用数据")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    @DataPermission(enable = false)
    public CommonResult<String> upgradeDataApp() {
        upgradeDataService.upgradeDataApp();
        return CommonResult.success("成功");
    }

    @PostMapping("/v1/market")
    @Operation(summary = "升级应用市场数据", description = "升级应用市场数据")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    @DataPermission(enable = false)
    public CommonResult<String> upgradeDataAppMarket() {
        upgradeDataService.upgradeDataAppMarket();
        return CommonResult.success("成功");
    }

    @PostMapping("/v1/plan")
    @Operation(summary = "升级计划数据", description = "升级计划数据")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    @DataPermission(enable = false)
    public CommonResult<String> upgradeDataCreativePlan() {
        upgradeDataService.upgradeDataCreativePlan();
        return CommonResult.success("成功");
    }

    @PostMapping("/v1/plan-batch")
    @Operation(summary = "升级计划批次数据", description = "升级计划批次数据")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    @DataPermission(enable = false)
    public CommonResult<String> upgradeDataCreativePlanBatch() {
        upgradeDataService.upgradeDataCreativePlanBatch();
        return CommonResult.success("成功");
    }

    @PostMapping("/v1/content")
    @Operation(summary = "升级内容任务数据", description = "升级内容任务数据")
    @ApiOperationSupport(order = 10, author = "nacoyer")
    @DataPermission(enable = false)
    public CommonResult<String> upgradeDataCreativeContent() {
        upgradeDataService.upgradeDataCreativeContent();
        return CommonResult.success("成功");
    }


}
