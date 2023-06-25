package com.starcloud.ops.business.app.controller.admin;


import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppMessageStatisticsListVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/llm/app/log")
@Tag(name = "应用执行日志")
public class AppLogController {


    @Resource
    private LogAppApi logAppApi;

    @Resource
    private LogAppConversationService appConversationService;

    @GetMapping("/infoPage")
    @Operation(summary = "获得应用执行日志信息分页")
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


    @PostMapping("/getAppMessageResult")
    @Operation(summary = "获取应用执行日志结果")
    @PreAuthorize("@ss.hasPermission('log:app-message:getAppMessageResult')")
    public CommonResult<LogAppMessageInfoRespVO> getAppMessageResult(@NotNull String appMessageUid) {
        return success(logAppApi.getAppMessageResult(appMessageUid));
    }


}
