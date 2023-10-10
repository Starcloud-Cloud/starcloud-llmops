package com.starcloud.ops.business.app.controller.admin.log;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.starcloud.ops.business.app.api.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageUidReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.response.AppLogConversationInfoRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.response.LogAppMessageStatisticsListVO;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListUidReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListReqVO;
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
    public CommonResult<List<LogAppMessageStatisticsListVO>> statistics(@Valid @RequestBody AppLogMessageStatisticsListReqVO query) {
        return success(appLogService.listLogMessageStatistics(query));
    }

    @PostMapping("/statisticsByAppUid")
    @DataPermission(enable = false)
    @Operation(summary = "根据应用 UID 获得应用执行统计列表")
    @ApiOperationSupport(order = 4, author = "nacoyer")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<List<LogAppMessageStatisticsListVO>> statisticsByAppUid(@Valid @RequestBody AppLogMessageStatisticsListUidReqVO query) {
        return success(appLogService.listLogMessageStatisticsByAppUid(query));
    }

    @PostMapping("/infoPage")
    @Operation(summary = "获得应用执行日志信息分页")
    @ApiOperationSupport(order = 5, author = "nacoyer")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<PageResult<AppLogConversationInfoRespVO>> infoPage(@Valid @RequestBody AppLogConversationInfoPageReqVO query) {
        return success(appLogService.pageLogConversation(query));
    }

    @PostMapping("/infoPageByAppUid")
    @DataPermission(enable = false)
    @Operation(summary = "根据应用 UID 获得应用执行日志信息分页")
    @ApiOperationSupport(order = 6, author = "nacoyer")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<PageResult<AppLogConversationInfoRespVO>> infoPageByAppUid(@Valid @RequestBody AppLogConversationInfoPageUidReqVO query) {
        return success(appLogService.pageLogConversationByAppUid(query));
    }

    @PostMapping("/infoPageByMarketUid")
    @Operation(summary = "根据应用市场 UID 获得应用执行日志信息分页")
    @ApiOperationSupport(order = 7, author = "nacoyer")
    public CommonResult<PageResult<AppLogMessageRespVO>> infoPageByMarketUid(@Valid @RequestBody AppLogConversationInfoPageUidReqVO query) {
        return success(appLogService.pageLogConversationByMarketUid(query));
    }

}
