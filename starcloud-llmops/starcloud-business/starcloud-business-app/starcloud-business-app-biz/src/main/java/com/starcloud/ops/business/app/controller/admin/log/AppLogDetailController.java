package com.starcloud.ops.business.app.controller.admin.log;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.api.log.vo.response.ImageLogMessageRespVO;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.log.api.message.vo.AppLogMessagePageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.http.POST;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-30
 */
@RestController
@RequestMapping("/llm/app/log/detail")
@Tag(name = "星河云海-应用执行日志详情", description = "应用执行日志详情相关接口")
public class AppLogDetailController {

    @Resource
    private AppLogService appLogService;

    @PostMapping("/app")
    @Operation(summary = "获得应用执行日志详情")
    public CommonResult<PageResult<AppLogMessageRespVO>> appLogMessageDetail(@Validated @RequestBody AppLogMessagePageReqVO query) {
        return CommonResult.success(appLogService.getLogAppMessageDetail(query));

    }

    @PostMapping("/chat")
    @Operation(summary = "获得聊天执行日志详情")
    public CommonResult<?> chatLogMessageDetail(@Validated @RequestBody AppLogMessagePageReqVO query) {
        query.setPageSize(1000);
        return CommonResult.success(appLogService.getChatMessageDetail(query));
    }

    @PostMapping("/image")
    @Operation(summary = "获取图片生成执行日志详情")
    public CommonResult<PageResult<ImageLogMessageRespVO>> imageLogMessageDetail(@Validated @RequestBody AppLogMessagePageReqVO query) {
        return CommonResult.success(appLogService.getLogImageMessageDetail(query));
    }

}
