package com.starcloud.ops.business.app.controller.admin.log;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.api.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.api.log.vo.response.ImageLogMessageRespVO;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.log.api.message.vo.AppLogMessagePageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

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

    @GetMapping("/app/{conversationUid}")
    @Operation(summary = "获得应用执行日志详情")
    public CommonResult<PageResult<AppLogMessageRespVO>> appLogMessageDetail(@Validated AppLogMessagePageReqVO query) {
        return CommonResult.success(appLogService.getLogAppMessageDetail(query));

    }

    @GetMapping("/chat/{conversationUid}")
    @Operation(summary = "获得聊天执行日志详情")
    public CommonResult<?> chatLogMessageDetail(@Validated AppLogMessagePageReqVO query) {
        return CommonResult.success(appLogService.getChatMessageDetail(query));
    }

    @GetMapping("/image/{conversationUid}")
    @Operation(summary = "获取图片生成执行日志详情")
    public CommonResult<PageResult<ImageLogMessageRespVO>> imageLogMessageDetail(@Validated AppLogMessagePageReqVO query) {
        return CommonResult.success(appLogService.getLogImageMessageDetail(query));
    }

}
