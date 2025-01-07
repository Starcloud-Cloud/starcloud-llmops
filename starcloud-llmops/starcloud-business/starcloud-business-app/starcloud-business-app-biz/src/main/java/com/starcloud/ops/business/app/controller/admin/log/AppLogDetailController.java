package com.starcloud.ops.business.app.controller.admin.log;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.starcloud.ops.business.app.controller.admin.log.vo.response.AppExecutedPromptRespVO;
import com.starcloud.ops.business.app.controller.admin.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.controller.admin.log.vo.response.ImageLogMessageRespVO;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessagePageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @DataPermission(enable = false)
    @Operation(summary = "获得应用执行日志详情")
    public CommonResult<AppLogMessageRespVO> appLogMessageDetail(@Validated @RequestBody LogAppMessagePageReqVO query) {
        // 默认查询生成式模型的日志，即应用的执行记录
        query.setAppMode(AppModelEnum.COMPLETION.name());
        return CommonResult.success(appLogService.getLogAppMessageDetail(query));
    }

    @PostMapping("/chat")
    @DataPermission(enable = false)
    @Operation(summary = "获得聊天执行日志详情")
    public CommonResult<?> chatLogMessageDetail(@Validated @RequestBody LogAppMessagePageReqVO query) {
        query.setPageSize(1000);
        // 默认查询聊天式模型的日志，即应用的执行记录
        query.setAppMode(AppModelEnum.CHAT.name());
        return CommonResult.success(appLogService.getChatMessageDetail(query));
    }

    @PostMapping("/image")
    @DataPermission(enable = false)
    @Operation(summary = "获取图片生成执行日志详情")
    public CommonResult<ImageLogMessageRespVO> imageLogMessageDetail(@Validated @RequestBody LogAppMessagePageReqVO query) {
        // 默认查询图片式模型的日志，即应用的执行记录
        query.setAppMode(AppModelEnum.IMAGE.name());
        return CommonResult.success(appLogService.getLogImageMessageDetail(query));
    }

    @PostMapping("/prompt")
    @DataPermission(enable = false)
    @Operation(summary = "获取图片生成执行日志详情")
    public CommonResult<AppExecutedPromptRespVO> getAppExecutedPrompt(@Validated @RequestBody LogAppMessagePageReqVO query) {
        query.setAppMode(AppModelEnum.COMPLETION.name());
        return CommonResult.success(appLogService.getAppExecutedPrompt(query));
    }

}
