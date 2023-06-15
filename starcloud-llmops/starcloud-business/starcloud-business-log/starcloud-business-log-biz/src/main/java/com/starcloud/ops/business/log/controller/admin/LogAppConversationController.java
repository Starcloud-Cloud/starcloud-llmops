package com.starcloud.ops.business.log.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.conversation.vo.*;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.*;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 应用执行日志会话")
@RestController
@RequestMapping("/log/app-conversation")
@Validated
public class LogAppConversationController {

    @Resource
    private LogAppConversationService appConversationService;

    @PostMapping("/create")
    @Operation(summary = "创建应用执行日志会话")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:create')")
    public CommonResult<Long> createAppConversation(@Valid @RequestBody LogAppConversationCreateReqVO createReqVO) {
        return success(appConversationService.createAppConversation(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新应用执行日志会话")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:update')")
    public CommonResult<Boolean> updateAppConversation(@Valid @RequestBody LogAppConversationUpdateReqVO updateReqVO) {
        appConversationService.updateAppConversation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除应用执行日志会话")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('log:app-conversation:delete')")
    public CommonResult<Boolean> deleteAppConversation(@RequestParam("id") Long id) {
        appConversationService.deleteAppConversation(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得应用执行日志会话")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<LogAppConversationRespVO> getAppConversation(@RequestParam("id") Long id) {
        LogAppConversationDO appConversation = appConversationService.getAppConversation(id);
        return success(LogAppConversationConvert.INSTANCE.convert(appConversation));
    }

    @GetMapping("/list")
    @Operation(summary = "获得应用执行日志会话列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<List<LogAppConversationRespVO>> getAppConversationList(@RequestParam("ids") Collection<Long> ids) {
        List<LogAppConversationDO> list = appConversationService.getAppConversationList(ids);
        return success(LogAppConversationConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得应用执行日志会话分页")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<PageResult<LogAppConversationRespVO>> getAppConversationPage(@Valid LogAppConversationPageReqVO pageVO) {
        PageResult<LogAppConversationDO> pageResult = appConversationService.getAppConversationPage(pageVO);
        return success(LogAppConversationConvert.INSTANCE.convertPage(pageResult));
    }


    @GetMapping("/infoPage")
    @Operation(summary = "获得应用执行日志会话分页")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<PageResult<LogAppConversationInfoRespVO>> getAppConversationPage(@Valid LogAppConversationInfoPageReqVO pageVO) {
        PageResult<LogAppConversationInfoPO> pageResult = appConversationService.getAppConversationInfoPage(pageVO);
        return success(LogAppConversationConvert.INSTANCE.convertInfoPage(pageResult));
    }

    @GetMapping("/statistics")
    @Operation(summary = "获得应用执行统计列表")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<List<LogAppMessageStatisticsListVO>> getAppMessageStatisticsList(@Valid LogAppMessageStatisticsListReqVO pageVO) {
        List<LogAppMessageStatisticsListPO> pageResult = appConversationService.getAppMessageStatisticsList(pageVO);
        return success(LogAppConversationConvert.INSTANCE.convertStatisticsList(pageResult));

    }

}
