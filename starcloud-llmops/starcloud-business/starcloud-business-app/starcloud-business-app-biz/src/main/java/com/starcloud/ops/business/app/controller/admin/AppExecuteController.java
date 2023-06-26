package com.starcloud.ops.business.app.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.AppWorkflowService;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/llm/app/execute")
@Tag(name = "星河云海-应用执行")
public class AppExecuteController {

    @Resource
    private AppWorkflowService appWorkflowService;

    @GetMapping("fireByUid")
    @Operation(summary = "根据 Uid 和场景 执行应用，默认执行第一个步骤", description = "根据 Uid 和场景 执行应用，默认执行第一个步骤")
    @ApiOperationSupport(order = 1, author = "nacoyer")
    public CommonResult<Void> execute(String appId, String scene) {
        appWorkflowService.fireByAppUid(appId, IEnumable.nameOf(scene, AppSceneEnum.class));
        return CommonResult.success(null);
    }
}
