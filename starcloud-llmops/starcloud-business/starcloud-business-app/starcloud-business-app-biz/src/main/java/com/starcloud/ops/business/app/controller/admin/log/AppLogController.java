package com.starcloud.ops.business.app.controller.admin.log;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppMessageStatisticsListVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.framework.common.api.dto.Option;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-30
 */
@RestController
@RequestMapping("/llm/app/log")
@Tag(name = "星河云海-应用执行日志", description = "应用执行日志相关接口")
public class AppLogController {

    @Resource
    private AppLogService appLogService;

    @GetMapping("/logMetaData/{type}")
    @Operation(summary = "日志元数据信息")
    @ApiOperationSupport(order = 1, author = "nacoyer")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<Map<String, List<Option>>> logMetaData(@PathVariable("type") String type) {
        return success(appLogService.logMetaData(type));
    }

    @GetMapping("/appMessageResult")
    @Operation(summary = "获取应用执行日志结果")
    @ApiOperationSupport(order = 2, author = "nacoyer")
    @PreAuthorize("@ss.hasPermission('log:app-message:getAppMessageResult')")
    public CommonResult<LogAppMessageInfoRespVO> getAppMessageResult(@RequestParam("appMessageUid") String appMessageUid) {
        return success(appLogService.getAppMessageResult(appMessageUid));
    }

    @PostMapping("/statistics")
    @Operation(summary = "获得应用执行统计列表")
    @ApiOperationSupport(order = 3, author = "nacoyer")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<List<LogAppMessageStatisticsListVO>> getAppMessageStatisticsList(@Valid @RequestBody LogAppMessageStatisticsListReqVO query) {
        return success(appLogService.appMessageStatisticsList(query));

    }

    @PostMapping("/infoPage")
    @Operation(summary = "获得应用执行日志信息分页")
    @ApiOperationSupport(order = 4, author = "nacoyer")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<PageResult<LogAppConversationInfoRespVO>> getAppConversationPage(@Valid @RequestBody LogAppConversationInfoPageReqVO query) {
        return success(appLogService.appConversationPage(query));
    }

}
