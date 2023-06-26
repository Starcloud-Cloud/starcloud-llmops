package com.starcloud.ops.business.app.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.app.controller.admin.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.AppWorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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

    @GetMapping("app")
    @Operation(summary = "执行应用")
    public CommonResult<Void> execute(AppExecuteReqVO executeReqVO, HttpServletResponse httpServletResponse) {
        appWorkflowService.fireByApp(executeReqVO.getAppUid(), AppSceneEnum.WEB_ADMIN, executeReqVO.getAppReqVO(), executeReqVO.getStepId(), executeReqVO.getConversationUid(), httpServletResponse);
        return CommonResult.success(null);
    }


    @GetMapping("market")
    @Operation(summary = "执行应用市场")
    public CommonResult<Void> executeMarket(AppExecuteReqVO executeReqVO, HttpServletResponse httpServletResponse) {
        appWorkflowService.fireByApp(executeReqVO.getAppUid(), AppSceneEnum.WEB_MARKET, executeReqVO.getAppReqVO(), executeReqVO.getStepId(), executeReqVO.getConversationUid(), httpServletResponse);
        return CommonResult.success(null);
    }


}
