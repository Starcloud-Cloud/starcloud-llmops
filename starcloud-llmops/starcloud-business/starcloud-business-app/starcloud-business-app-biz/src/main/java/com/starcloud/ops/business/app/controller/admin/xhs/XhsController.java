package com.starcloud.ops.business.app.controller.admin.xhs;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.api.xhs.XhsAppResponse;
import com.starcloud.ops.business.app.api.xhs.XhsExecuteRequest;
import com.starcloud.ops.business.app.api.xhs.XhsExecuteResponse;
import com.starcloud.ops.business.app.service.xhs.XhsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 应用执行
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-26
 */
@RestController
@RequestMapping("/llm/app/xhs")
@Tag(name = "星河云海-小红书", description = "星河云海应用执行, 应用和应用市场执行")
public class XhsController {

    @Resource
    private XhsService xhsService;

    @GetMapping("/app/{uid}")
    @Operation(summary = "获取应用信息")
    public CommonResult<XhsAppResponse> getApp(@PathVariable("uid") String uid) {
        return CommonResult.success(xhsService.getApp(uid));
    }

    @PostMapping(value = "/execute")
    @Operation(summary = "执行")
    public CommonResult<XhsExecuteResponse> execute(@RequestBody XhsExecuteRequest request) {
        return CommonResult.success(xhsService.execute(request));
    }

}
