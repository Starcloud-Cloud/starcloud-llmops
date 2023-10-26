package com.starcloud.ops.business.log.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageInfoRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 应用执行日志结果")
@RestController
@RequestMapping("/log/app-message")
@Validated
public class AppLogAppMessageController {


    @Resource
    private LogAppApi logAppApi;

    @PostMapping("/getAppMessageResult")
    @Operation(summary = "获取应用执行日志结果")
    @PreAuthorize("@ss.hasPermission('log:app-message:getAppMessageResult')")
    public CommonResult<LogAppMessageInfoRespVO> getAppMessageResult(@NotNull String appMessageUid) {
        return success(logAppApi.getAppMessageResult(appMessageUid));
    }


}