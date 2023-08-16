package com.starcloud.ops.business.app.controller.admin.log;


import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.util.IdentifyUserUtils;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppMessageStatisticsListVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.framework.common.api.dto.Option;
import com.starcloud.ops.framework.common.api.enums.IEnumable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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
    private LogAppApi logAppApi;

    @Resource
    private LogAppConversationService appConversationService;

    @Resource
    private IdentifyUserUtils identifyUserUtils;

    @GetMapping("/timeType")
    @Operation(summary = "时间类型列表")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<List<Option>> getTimeList() {
        List<LogTimeTypeEnum> values = IEnumable.values(LogTimeTypeEnum.class);
        return success(values.stream().map(item -> {
            Option option = Option.of(item.getLabelEn(), item.name());
            Locale locale = LocaleContextHolder.getLocale();
            if (Locale.CHINA.equals(locale)) {
                option.setLabel(item.getLabel());
            }
            return option;
        }).collect(Collectors.toList()));

    }

    @PostMapping("/statistics")
    @Operation(summary = "获得应用执行统计列表")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<List<LogAppMessageStatisticsListVO>> getAppMessageStatisticsList(@Valid @RequestBody LogAppMessageStatisticsListReqVO pageVO) {
        List<LogAppMessageStatisticsListPO> pageResult = appConversationService.getAppMessageStatisticsList(pageVO);
        return success(LogAppConversationConvert.INSTANCE.convertStatisticsList(pageResult));

    }

    @PostMapping("/infoPage")
    @Operation(summary = "获得应用执行日志信息分页")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<PageResult<LogAppConversationInfoRespVO>> getAppConversationPage(@Valid @RequestBody LogAppConversationInfoPageReqVO pageVO) {
        PageResult<LogAppConversationInfoPO> pageResult = appConversationService.getAppConversationInfoPage(pageVO);
        PageResult<LogAppConversationInfoRespVO> result = LogAppConversationConvert.INSTANCE.convertInfoPage(pageResult);
        List<LogAppConversationInfoRespVO> list = result.getList();
        List<LogAppConversationInfoRespVO> collect = CollectionUtil.emptyIfNull(list).stream()
                .peek(item -> item.setAppExecutor(identifyUserUtils.identifyUser(item.getCreator(), item.getEndUser())))
                .collect(Collectors.toList());
        result.setList(collect);
        return success(result);
    }


    @GetMapping("/appMessageResult")
    @Operation(summary = "获取应用执行日志结果")
    @PreAuthorize("@ss.hasPermission('log:app-message:getAppMessageResult')")
    public CommonResult<LogAppMessageInfoRespVO> getAppMessageResult(@NotNull String appMessageUid) {
        return success(logAppApi.getAppMessageResult(appMessageUid));
    }


}
