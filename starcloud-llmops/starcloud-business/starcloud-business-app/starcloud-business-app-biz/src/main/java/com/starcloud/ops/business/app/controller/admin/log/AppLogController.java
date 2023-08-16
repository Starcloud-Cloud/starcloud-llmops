package com.starcloud.ops.business.app.controller.admin.log;


import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.util.DataPermissionUtils;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppMessageStatisticsListVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.enums.LogQueryTypeEnum;
import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.framework.common.api.dto.Option;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/logMetaData/{type}")
    @Operation(summary = "日志元数据信息")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<Map<String, List<Option>>> logMetaData(@PathVariable("type") String type) {
        Map<String, List<Option>> logMetaDataMap = new HashMap<>(4);
        // 时间类型
        logMetaDataMap.put("timeType", LogTimeTypeEnum.getOptions());
        // 模型类型
        logMetaDataMap.put("appMode", AppModelEnum.getOptions());
        // 场景类型
        logMetaDataMap.put("appScene", getSceneOptions(type));

        return success(logMetaDataMap);
    }

    @PostMapping("/statistics")
    @Operation(summary = "获得应用执行统计列表")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<List<LogAppMessageStatisticsListVO>> getAppMessageStatisticsList(@Valid @RequestBody LogAppMessageStatisticsListReqVO pageVO) {
        pageVO.setAppModeList(getAppModeList(pageVO.getType()));
        List<LogAppMessageStatisticsListPO> pageResult = appConversationService.getAppMessageStatisticsList(pageVO);
        return success(LogAppConversationConvert.INSTANCE.convertStatisticsList(pageResult));

    }

    @PostMapping("/infoPage")
    @Operation(summary = "获得应用执行日志信息分页")
    @PreAuthorize("@ss.hasPermission('log:app-conversation:query')")
    public CommonResult<PageResult<LogAppConversationInfoRespVO>> getAppConversationPage(@Valid @RequestBody LogAppConversationInfoPageReqVO pageVO) {
        pageVO.setAppModeList(getAppModeList(pageVO.getType()));
        PageResult<LogAppConversationInfoPO> pageResult = appConversationService.getAppConversationInfoPage(pageVO);
        PageResult<LogAppConversationInfoRespVO> result = LogAppConversationConvert.INSTANCE.convertInfoPage(pageResult);
        List<LogAppConversationInfoRespVO> list = result.getList();
        List<LogAppConversationInfoRespVO> collect = CollectionUtil.emptyIfNull(list).stream()
                .peek(item -> item.setAppExecutor(DataPermissionUtils.identify(item.getCreator(), item.getEndUser())))
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

    /**
     * 获取场景列表
     *
     * @param type 类型
     * @return 场景列表
     */
    private static List<Option> getSceneOptions(String type) {
        // 生成记录
        if (LogQueryTypeEnum.GENERATE_RECORD.name().equals(type)) {
            String permission = DataPermissionUtils.getDeptDataPermission();
            if (DataPermissionUtils.ALL.equals(permission)) {
                return AppSceneEnum.getOptions();
            } else {
                return AppSceneEnum.getOptions(AppSceneEnum.GENERATE_RECORD_BASE_SCENES);
            }
        }
        // 应用分析
        if (LogQueryTypeEnum.APP_ANALYSIS.name().equals(type)) {
            return AppSceneEnum.getOptions(AppSceneEnum.APP_ANALYSIS_SCENES);
        }
        // 聊天分析
        if (LogQueryTypeEnum.CHAT_ANALYSIS.name().equals(type)) {
            return AppSceneEnum.getOptions(AppSceneEnum.CHAT_ANALYSIS_SCENES);
        }
        throw ServiceExceptionUtil.exception(new ErrorCode(1000001, "type 不支持"));
    }

    /**
     * 获取应用模型列表
     *
     * @param type 类型
     * @return 应用模型列表
     */
    private static List<String> getAppModeList(String type) {
        if (LogQueryTypeEnum.GENERATE_RECORD.name().equals(type)) {
            String permission = DataPermissionUtils.getDeptDataPermission();
            if (!DataPermissionUtils.ALL.equals(permission)) {
                return AppSceneEnum.GENERATE_RECORD_BASE_SCENES.stream().map(AppSceneEnum::name).collect(Collectors.toList());
            }
        }
        return null;
    }
}
