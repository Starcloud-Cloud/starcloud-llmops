package com.starcloud.ops.business.app.controller.admin.xhs.upgrade;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @DataPermission(enable = false)
    public CommonResult<String> upgradeDataApp() {
        upgradeDataService.upgradeDataApp();
        return CommonResult.success("成功");
    }

    @PostMapping("/v1/market")
    @DataPermission(enable = false)
    public CommonResult<String> upgradeDataAppMarket() {
        upgradeDataService.upgradeDataAppMarket();
        return CommonResult.success("成功");
    }

    @PostMapping("/v1/plan")
    @DataPermission(enable = false)
    public CommonResult<String> upgradeDataCreativePlan() {
        upgradeDataService.upgradeDataCreativePlan();
        return CommonResult.success("成功");
    }

    @PostMapping("/v1/plan-batch")
    @DataPermission(enable = false)
    public CommonResult<String> upgradeDataCreativePlanBatch(@RequestParam("index") Integer index, @RequestParam("size") Integer size) {
        upgradeDataService.upgradeDataCreativePlanBatch(index, size);
        return CommonResult.success("成功");
    }

    @PostMapping("/v1/content")
    @DataPermission(enable = false)
    public CommonResult<String> upgradeDataCreativeContent(@RequestParam("index") Integer index, @RequestParam("size") Integer size) {
        upgradeDataService.upgradeDataCreativeContent(index, size);
        return CommonResult.success("成功");
    }

    @PostMapping("/v2/content")
    @DataPermission(enable = false)
    public CommonResult<String> upgradeDataCreativeContent2(@RequestParam("index") Integer index, @RequestParam("size") Integer size) {
        upgradeDataService.upgradeDataContent(index, size);
        return CommonResult.success("成功");
    }


}
