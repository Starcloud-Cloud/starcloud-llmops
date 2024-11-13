package com.starcloud.ops.business.app.service.log.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.api.image.vo.response.BaseImageResponse;
import com.starcloud.ops.business.app.controller.admin.log.vo.request.AppLogMessageQuery;
import com.starcloud.ops.business.app.controller.admin.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.controller.admin.log.vo.response.ImageLogMessageRespVO;
import com.starcloud.ops.business.app.controller.admin.xhs.content.vo.response.CreativeContentRespVO;
import com.starcloud.ops.business.app.convert.xhs.content.CreativeContentConvert;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.dal.databoject.xhs.content.CreativeContentDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.mysql.publish.AppPublishMapper;
import com.starcloud.ops.business.app.dal.mysql.xhs.content.CreativeContentMapper;
import com.starcloud.ops.business.app.enums.RecommendAppEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.model.content.CreativeContentExecuteResult;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.PageUtil;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.AppLogConversationInfoPageUidReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.query.LogAppConversationPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.response.AppLogConversationInfoRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.response.LogAppMessageStatisticsListVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.AppLogMessageStatisticsListUidReqVO;
import com.starcloud.ops.business.log.api.message.vo.query.LogAppMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageRespVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.convert.LogAppMessageConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import com.starcloud.ops.business.log.enums.ErrorCodeConstants;
import com.starcloud.ops.business.log.enums.LogQueryTypeEnum;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.api.rights.dto.StatisticsUserRightReqDTO;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.APP_NON_EXISTENT;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.MARKET_APP_NON_EXISTENT;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.PUBLISH_APP_NON_EXISTENT;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-09
 */
@Slf4j
@Service
public class AppLogServiceImpl implements AppLogService {

    @Resource
    private LogAppApi logAppApi;

    @Resource
    private LogAppMessageService logAppMessageService;

    @Resource
    private LogAppMessageMapper logAppMessageMapper;

    @Resource
    private LogAppConversationService logAppConversationService;

    @Resource
    private ChatService chatService;

    @Resource
    private AppPublishChannelService appPublishChannelService;

    @Resource
    private AppMapper appMapper;

    @Resource
    private AppMarketMapper appMarketMapper;

    @Resource
    private AppPublishMapper appPublishMapper;

    @Resource
    private CreativeContentMapper creativeContentMapper;

    @Resource
    private AdminUserRightsApi adminUserRightsApi;

    /**
     * 日志元数据
     *
     * @param type 类型
     * @return 日志元数据
     */
    @Override
    public Map<String, List<Option>> logMetaData(String type) {
        Map<String, List<Option>> logMetaMap = new HashMap<>(4);
        // 时间类型
        logMetaMap.put("timeType", LogTimeTypeEnum.getOptions());
        // 模型类型
        logMetaMap.put("appMode", AppModelEnum.getOptions());
        // 场景类型
        logMetaMap.put("appScene", getSceneOptions(type));

        return logMetaMap;
    }

    /**
     * 根据条件查询日志消息数量
     *
     * @param query 查询条件
     * @return 日志消息数量
     */
    @Override
    public Page<LogAppMessageRespVO> pageAppLogMessage(AppLogMessageQuery query) {
        LambdaQueryWrapper<LogAppMessageDO> wrapper = Wrappers.lambdaQuery(LogAppMessageDO.class);
        wrapper.eq(LogAppMessageDO::getAppUid, query.getAppUid());
        wrapper.eq(StringUtils.isNotBlank(query.getAppMode()), LogAppMessageDO::getAppMode, query.getAppMode());
        wrapper.eq(StringUtils.isNotBlank(query.getUserId()), LogAppMessageDO::getCreator, query.getUserId());
        wrapper.eq(StringUtils.isNotBlank(query.getEndUser()), LogAppMessageDO::getEndUser, query.getEndUser());
        wrapper.eq(StringUtils.isNotBlank(query.getFromScene()), LogAppMessageDO::getFromScene, query.getFromScene());
        // 时间间隔和时间间隔单位不为空情况
        if (Objects.nonNull(query.getTimeInterval()) && Objects.nonNull(query.getTimeUnit())) {
            LocalDateTime startTime = LocalDateTime.now();
            LocalDateTime endTime = startTime.minus(query.getTimeInterval(), query.getTimeUnit());
            wrapper.between(LogAppMessageDO::getCreateTime, startTime, endTime);
        }
        if (CollectionUtil.isEmpty(query.getSorts())) {
            wrapper.orderByDesc(LogAppMessageDO::getCreateTime);
        }
        Page<LogAppMessageDO> page = logAppMessageMapper.selectPage(PageUtil.page(query), wrapper);
        return LogAppMessageConvert.INSTANCE.convertPage(page);
    }

    /**
     * 获取应用执行日志消息
     *
     * @param appMessageUid 应用执行日志uid
     * @return 应用执行日志列表
     */
    @Override
    public LogAppMessageInfoRespVO getAppMessageResult(String appMessageUid) {
        return logAppApi.getAppMessageResult(appMessageUid);
    }

    /**
     * 获取应用执行日志消息统计数据
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    @Override
    public List<LogAppMessageStatisticsListVO> listLogMessageStatistics(AppLogMessageStatisticsListReqVO query) {
        if (StringUtils.isBlank(query.getFromScene())) {
            query.setFromSceneList(getFromSceneList());
        }
        if (UserUtils.isNotAdmin()) {
            query.setUserId(null);
        }
        // 时间类型默认值
        query.setTimeType(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType());
        List<LogAppMessageStatisticsListPO> pageResult = logAppMessageService.listLogAppMessageStatistics(query);
        Boolean isAdmin = UserUtils.isAdmin();
        pageResult = pageResult.stream().peek(item -> {
            // 非管理员不能查看，平均耗时
            if (!isAdmin) {
                item.setCompletionAvgElapsed(new BigDecimal("0"));
                item.setImageAvgElapsed(new BigDecimal("0"));
                item.setFeedbackLikeCount(0);
                item.setTokens(0);
                item.setCompletionTokens(0);
                item.setChatTokens(0);
            }
        }).collect(Collectors.toList());
        return LogAppConversationConvert.INSTANCE.convertStatisticsList(pageResult);
    }

    /**
     * 根据 应用 UID 获取应用执行日志消息统计数据列表 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    @Override
    public List<LogAppMessageStatisticsListVO> listLogMessageStatisticsByAppUid(AppLogMessageStatisticsListUidReqVO query) {
        // 查询应用类型
        AppDO app = appMapper.get(query.getAppUid(), Boolean.TRUE);
        AppValidate.notNull(app, APP_NON_EXISTENT, query.getAppUid());
        Boolean isAdmin = UserUtils.isAdmin();
        // 应用模型为 COMPLETION 时，说明为应用场景下的应用分析
        if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {
            List<AppSceneEnum> appAnalysisScenes = new ArrayList<>(AppSceneEnum.APP_ANALYSIS_SCENES);
            if (isAdmin) {
                appAnalysisScenes.add(AppSceneEnum.OPTIMIZE_PROMPT);
                appAnalysisScenes.add(AppSceneEnum.LISTING_GENERATE);
            }
            if (StringUtils.isNotBlank(query.getFromScene()) && !appAnalysisScenes.contains(AppSceneEnum.valueOf(query.getFromScene()))) {
                throw ServiceExceptionUtil.exception(new ErrorCode(3000001, "应用分析时，应用场景[fromScene]不支持！"));
            }
        }

        // 应用模型为 CHAT 时，说明为聊天场景下的应用分析
        if (AppModelEnum.CHAT.name().equals(app.getModel())) {
            if (StringUtils.isNotBlank(query.getFromScene()) && !AppSceneEnum.isChatAnalysisScene(AppSceneEnum.valueOf(query.getFromScene()))) {
                throw ServiceExceptionUtil.exception(new ErrorCode(3000001, "聊天应用分析时，应用场景[fromScene]不支持！"));
            }
        }

        // 执行场景不为空的情况
        if (StringUtils.isNotBlank(query.getFromScene())) {
            // 如果是应用市场支持的场景，则需要查询应用市场执行信息。
            if (AppSceneEnum.isMarketScene(AppSceneEnum.valueOf(query.getFromScene()))) {
                String marketUid = getMarketUidByApp(app);
                // 未获取到应用市场 UID，则直接返回空数据。不需要再走数据库查询
                if (StringUtils.isBlank(marketUid)) {
                    return Collections.emptyList();
                }
                query.setMarketUid(marketUid);
            } else {
                query.setMarketUid(null);
            }
        } else {
            // 执行场景为空的情况，需要查询应用市场执行信息。
            query.setMarketUid(getMarketUidByApp(app));
        }

        if (!isAdmin) {
            query.setUserId(null);
        }
        // 时间类型默认值
        query.setTimeType(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType());
        List<LogAppMessageStatisticsListPO> pageResult = logAppMessageService.listLogAppMessageStatistics(query);
        pageResult = pageResult.stream().peek(item -> {
            // 非管理员不能查看，平均耗时
            if (!isAdmin) {
                item.setCompletionAvgElapsed(new BigDecimal("0"));
                item.setImageAvgElapsed(new BigDecimal("0"));
                item.setFeedbackLikeCount(0);
                item.setTokens(0);
                item.setCompletionTokens(0);
                item.setChatTokens(0);
            }
        }).collect(Collectors.toList());
        return LogAppConversationConvert.INSTANCE.convertStatisticsList(pageResult);
    }

    /**
     * 分页查询应用执行日志会话数据
     *
     * @param query 查询条件
     * @return 应用执行日志会话数据
     */
    @Override
    @DataPermission
    public PageResult<AppLogConversationInfoRespVO> pageLogConversation(AppLogConversationInfoPageReqVO query) {
        if (StringUtils.isBlank(query.getFromScene())) {
            query.setFromSceneList(getFromSceneList());
        }
        if (UserUtils.isNotAdmin()) {
            query.setUserId(null);
        }
        // 时间类型默认值
        query.setTimeType(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType());
        PageResult<LogAppConversationInfoPO> pageResult = logAppConversationService.pageLogAppConversation(query);
        return transformAppLogConversationInfoPage(pageResult);
    }

    /**
     * 根据应用市场 UID 分页查询应用执行日志会话数据 <br>
     *
     * @param query 查询条件
     * @return 应用市场执行日志会话数据
     */
    @Override
    public PageResult<AppLogMessageRespVO> pageLogConversationByMarketUid(AppLogConversationInfoPageUidReqVO query) {
        AppValidate.notBlank(query.getMarketUid(), new ErrorCode(3000001, "应用市场 UID 不能为空"));
        if (StringUtils.isNotBlank(query.getFromScene())) {
            if (!AppSceneEnum.isMarketScene(AppSceneEnum.valueOf(query.getFromScene()))) {
                throw ServiceExceptionUtil.exception(new ErrorCode(3000001, "不支持的场景"));
            }
        }
        AppMarketDO appMarket = appMarketMapper.get(query.getMarketUid(), Boolean.TRUE);
        AppValidate.notNull(appMarket, MARKET_APP_NON_EXISTENT, query.getMarketUid());

        // 时间类型默认值
        query.setTimeType(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType());
        query.setStatus(LogStatusEnum.SUCCESS.name());
        PageResult<LogAppConversationInfoPO> pageResult = logAppConversationService.pageLogAppConversation(query);
        if (pageResult.getTotal() == 0) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }

        List<String> conversationUidList = CollectionUtil.emptyIfNull(pageResult.getList()).stream().map(LogAppConversationInfoPO::getUid).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(conversationUidList)) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        Map<String, List<LogAppMessageDO>> messgaeMap = logAppMessageService.mapAppLogMessageAppConversationUid(conversationUidList);
        if (CollectionUtil.isEmpty(messgaeMap)) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        List<AppLogMessageRespVO> collect = messgaeMap.values().stream()
                .filter(CollectionUtil::isNotEmpty)
                .map(list -> list.get(0))
                .filter(Objects::nonNull)
                .map(item -> transformAppLogMessage(item, appMarket.getName()))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());
        return new PageResult<>(collect, pageResult.getTotal());
    }

    /**
     * 根据 应用 UID 分页查询应用执行日志会话数据 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 应用执行日志会话数据
     */
    @Override
    public PageResult<AppLogConversationInfoRespVO> pageLogConversationByAppUid(AppLogConversationInfoPageUidReqVO query) {
        // 应用 UID 不能为空
        AppValidate.notBlank(query.getAppUid(), new ErrorCode(3000001, "应用分析时，应用UID[appUid]为必填项"));
        // 查询应用类型
        AppDO app = appMapper.get(query.getAppUid(), Boolean.TRUE);
        AppValidate.notNull(app, APP_NON_EXISTENT, query.getAppUid());
        Boolean isAdmin = UserUtils.isAdmin();

        // 应用模型为 COMPLETION 时，说明为应用场景下的应用分析
        if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {
            List<AppSceneEnum> appAnalysisScenes = new ArrayList<>(AppSceneEnum.APP_ANALYSIS_SCENES);
            if (isAdmin) {
                appAnalysisScenes.add(AppSceneEnum.OPTIMIZE_PROMPT);
                appAnalysisScenes.add(AppSceneEnum.LISTING_GENERATE);
            }
            if (StringUtils.isNotBlank(query.getFromScene()) && !appAnalysisScenes.contains(AppSceneEnum.valueOf(query.getFromScene()))) {
                throw ServiceExceptionUtil.exception(new ErrorCode(3000001, "应用分析时，应用场景[fromScene]不支持！"));
            }
        }

        // 应用模型为 CHAT 时，说明为聊天场景下的应用分析
        if (AppModelEnum.CHAT.name().equals(app.getModel())) {
            if (StringUtils.isNotBlank(query.getFromScene()) && !AppSceneEnum.isChatAnalysisScene(AppSceneEnum.valueOf(query.getFromScene()))) {
                throw ServiceExceptionUtil.exception(new ErrorCode(3000001, "聊天应用分析时，应用场景[fromScene]不支持！"));
            }
        }

        // 执行场景不为空的情况
        if (StringUtils.isNotBlank(query.getFromScene())) {
            // 如果是应用市场支持的场景，则需要查询应用市场执行信息。
            if (AppSceneEnum.isMarketScene(AppSceneEnum.valueOf(query.getFromScene()))) {
                String marketUid = getMarketUidByApp(app);
                // 未获取到应用市场 UID，则直接返回空数据。不需要再走数据库查询
                if (StringUtils.isBlank(marketUid)) {
                    return new PageResult<>(Collections.emptyList(), 0L);
                }
                query.setMarketUid(marketUid);
            } else {
                // 如果执行场景不是应用市场的场景，则不需要查询应用市场执行信息。
                query.setMarketUid(null);
            }
        } else {
            // 执行场景为空的情况，需要查询应用市场执行信息。
            query.setMarketUid(getMarketUidByApp(app));
        }
        if (!isAdmin) {
            query.setUserId(null);
        }
        // 时间类型默认值
        query.setTimeType(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType());
        PageResult<LogAppConversationInfoPO> pageResult = logAppConversationService.pageLogAppConversation(query);
        return transformAppLogConversationInfoPage(pageResult);
    }

    /**
     * 分页查询应用执行日志消息数据
     *
     * @param query 查询条件
     * @return 应用执行日志消息数据
     */
    @Override
    public PageResult<ImageLogMessageRespVO> pageImageRecord(LogAppConversationPageReqVO query) {

        // 不传入场景时，返回空数据
        if (CollectionUtil.isEmpty(query.getScenes())) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }

        query.setCreator(String.valueOf(SecurityFrameworkUtils.getLoginUserId()));
        PageResult<LogAppConversationDO> page = logAppConversationService.pageAppLogConversation(query);
        if (CollectionUtil.isEmpty(page.getList())) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }

        // 获取会话 UID 列表
        List<String> conversationUidList = page.getList().stream().map(LogAppConversationDO::getUid).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(conversationUidList)) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        Map<String, List<LogAppMessageDO>> messgaeMap = logAppMessageService.mapAppLogMessageAppConversationUid(conversationUidList);
        if (CollectionUtil.isEmpty(messgaeMap)) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        List<ImageLogMessageRespVO> collect = messgaeMap.values().stream()
                .filter(CollectionUtil::isNotEmpty)
                .map(list -> list.get(0))
                .filter(Objects::nonNull)
                .map(item -> transformImageLogMessage(item, Optional.ofNullable(RecommendAppEnum.of(item.getAppUid())).map(RecommendAppEnum::getLabel).orElse("")))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());
        return new PageResult<>(collect, page.getTotal());
    }

    /**
     * 获取文本生成消息详情
     *
     * @param query 查询条件
     * @return AppLogMessageRespVO
     */
    @Override
    public AppLogMessageRespVO getLogAppMessageDetail(LogAppMessagePageReqVO query) {
        // 获取会话记录
        LogAppConversationDO appConversation = logAppConversationService.getAppLogConversation(query.getAppConversationUid());
        AppValidate.notNull(appConversation, ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS_UID, query.getAppConversationUid());

        // 获取消息列表
        PageResult<LogAppMessageDO> appMessagePage = logAppMessageService.pageAppLogMessage(query);
        List<LogAppMessageDO> appMessageList = appMessagePage.getList();
        if (query.getThrowIfEmpty() != null && !query.getThrowIfEmpty()) {
            return null;
        }
        // 校验日志消息是否存在
        AppValidate.notEmpty(appMessageList, ErrorCodeConstants.APP_MESSAGE_NOT_EXISTS);

        // 不管是多步骤执行，还是单步骤执行，都是取最新一条记录转换返回。
        LogAppMessageDO logAppMessage = appMessageList.get(0);
        AppLogMessageRespVO appLogMessageResponse = transformAppLogMessage(logAppMessage, appConversation);

        // 如果是通过渠道执行的，需要查询渠道信息
        if (StringUtils.isNotBlank(logAppMessage.getMediumUid()) && !AppSceneEnum.LISTING_GENERATE.name().equals(appLogMessageResponse.getFromScene())) {
            AppPublishChannelRespVO channel = appPublishChannelService.getByMediumUid(logAppMessage.getMediumUid());
            appLogMessageResponse.setChannel(channel);
        }
        // 媒体类型处理
        if (AppTypeEnum.MEDIA_MATRIX.name().equals(appLogMessageResponse.getAppInfo().getType())) {
            LambdaQueryWrapper<CreativeContentDO> wrapper = Wrappers.lambdaQuery(CreativeContentDO.class);
            wrapper.eq(CreativeContentDO::getConversationUid, appLogMessageResponse.getConversationUid());
            CreativeContentDO content = creativeContentMapper.selectOne(wrapper);
            CreativeContentRespVO contentResponse = CreativeContentConvert.INSTANCE.convert(content);
            CreativeContentExecuteResult executeResult = contentResponse.getExecuteResult();
            appLogMessageResponse.setExecuteResult(executeResult);
            appLogMessageResponse.setContentUid(content.getUid());
            appLogMessageResponse.setPlanUid(content.getPlanUid());
            appLogMessageResponse.setPlanBatchUid(content.getBatchUid());
        }
        return appLogMessageResponse;
    }

    /**
     * 获取聊天详情
     *
     * @param query 查询条件
     * @return AppLogMessageRespVO
     */
    @Override
    public PageResult<AppLogMessageRespVO> getChatMessageDetail(LogAppMessagePageReqVO query) {
        LogAppConversationDO appConversation = logAppConversationService.getAppLogConversation(query.getAppConversationUid());
        AppValidate.notNull(appConversation, ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS_UID, query.getAppConversationUid());
        String images;

        if (AppSceneEnum.CHAT_MARKET.name().equals(query.getFromScene())) {
            AppMarketDO appMarketDO = appMarketMapper.get(appConversation.getAppUid(), Boolean.TRUE);
            AppValidate.notNull(appMarketDO, APP_NON_EXISTENT, appConversation.getAppUid());
            images = appMarketDO.getImages();
        } else {
            AppDO app = appMapper.get(appConversation.getAppUid(), Boolean.TRUE);
            AppValidate.notNull(app, APP_NON_EXISTENT, appConversation.getAppUid());
            images = app.getImages();
        }

        PageResult<LogAppMessageDO> messagePageResult = chatService.chatHistory(query.getAppConversationUid(), query.getPageNo(), query.getPageSize());
        List<LogAppMessageDO> chatMessageList = messagePageResult.getList();
        // 校验日志消息是否存在
        AppValidate.notEmpty(chatMessageList, ErrorCodeConstants.APP_MESSAGE_NOT_EXISTS);

        String finalImages = images;
        List<AppLogMessageRespVO> collect = CollectionUtil.emptyIfNull(chatMessageList).stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    AppLogMessageRespVO appLogMessageResponse = transformAppLogMessage(item, appConversation, finalImages);
                    if (StringUtils.isNotBlank(item.getMediumUid())) {
                        AppPublishChannelRespVO channel = appPublishChannelService.getByMediumUid(item.getMediumUid());
                        appLogMessageResponse.setChannel(channel);
                    }
                    return appLogMessageResponse;
                })
                .collect(Collectors.toList());
        return new PageResult<>(collect, messagePageResult.getTotal());
    }

    /**
     * 获取图片生成消息详情
     *
     * @param query 查询条件
     * @return ImageRespVO
     */
    @Override
    public ImageLogMessageRespVO getLogImageMessageDetail(LogAppMessagePageReqVO query) {
        // 获取会话
        LogAppConversationDO appConversation = logAppConversationService.getAppLogConversation(query.getAppConversationUid());
        AppValidate.notNull(appConversation, ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS_UID, query.getAppConversationUid());

        // 获取消息列表
        PageResult<LogAppMessageDO> appMessagePage = logAppMessageService.pageAppLogMessage(query);
        List<LogAppMessageDO> appMessageList = appMessagePage.getList();
        // 校验日志消息是否存在
        AppValidate.notEmpty(appMessageList, ErrorCodeConstants.APP_MESSAGE_NOT_EXISTS);

        // 取第一条数据
        LogAppMessageDO logAppMessage = appMessageList.get(0);
        ImageLogMessageRespVO imageLogMessageResponse = transformImageLogMessage(logAppMessage, appConversation);

        if (Objects.isNull(imageLogMessageResponse.getImageInfo()) || CollectionUtil.isEmpty(imageLogMessageResponse.getImageInfo().getImages())) {
            throw ServiceExceptionUtil.exception(new ErrorCode(3000001, "图片生成失败，图片信息为空！无法查看详情！"));
        }

        // 如果是通过渠道执行的，需要查询渠道信息
        if (StringUtils.isNotBlank(logAppMessage.getMediumUid())) {
            AppPublishChannelRespVO channel = appPublishChannelService.getByMediumUid(logAppMessage.getMediumUid());
            imageLogMessageResponse.setChannel(channel);
        }

        return imageLogMessageResponse;
    }

    /**
     * 根据应用信息获取应用市场 UID
     *
     * @param app 应用信息
     * @return 应用市场UID
     */
    private String getMarketUidByApp(AppDO app) {
        // 如果 publishUid 为空，说明此应用未发布成功到应用市场过，直接返回空数据
        String publishUid = app.getPublishUid();
        if (StringUtils.isBlank(publishUid)) {
            return null;
        }

        // 查询发布信息并校验是否存在
        String appPublishUid = AppUtils.obtainUid(publishUid);
        AppPublishDO appPublish = appPublishMapper.get(appPublishUid, Boolean.TRUE);
        AppValidate.notNull(appPublish, PUBLISH_APP_NON_EXISTENT, appPublishUid);

        // marketUid 为空，说明可能数据有问题，直接返回空数据
        String marketUid = appPublish.getMarketUid();
        if (StringUtils.isBlank(marketUid)) {
            return null;
        }

        // 查询应用市场信息并校验是否存在
        AppMarketDO appMarket = appMarketMapper.get(marketUid, Boolean.TRUE);
        if (Objects.isNull(appMarket)) {
            return null;
        }

        return appMarket.getUid();
    }

    /**
     * 处理应用执行日志会话分页数据
     *
     * @param pageResult 应用执行日志会话分页数据
     * @return 应用执行日志会话分页数据
     */
    private PageResult<AppLogConversationInfoRespVO> transformAppLogConversationInfoPage(PageResult<LogAppConversationInfoPO> pageResult) {

        // 如果没有数据，则直接返回空数据
        if (CollectionUtil.isEmpty(pageResult.getList())) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }

        // 处理返回数据
        PageResult<AppLogConversationInfoRespVO> result = LogAppConversationConvert.INSTANCE.convertInfoPage(pageResult);
        List<AppLogConversationInfoRespVO> list = result.getList();

        // 获取会话 UID 列表
        List<String> conversationUidList = list.stream().map(AppLogConversationInfoRespVO::getUid)
                .distinct().collect(Collectors.toList());
        List<StatisticsUserRightReqDTO> rightList = adminUserRightsApi.statisticsUserRightsByBizId(conversationUidList);
        Map<String, StatisticsUserRightReqDTO> rightMap = CollectionUtil.emptyIfNull(rightList).stream()
                .collect(Collectors.toMap(StatisticsUserRightReqDTO::getBizId, Function.identity()));

        // 获取会话创建人
        List<Long> userIdList = list.stream()
                .map(AppLogConversationInfoRespVO::getCreator)
                .filter(StringUtils::isNotBlank)
                .filter(item -> !"null".equals(item))
                .map(Long::parseLong)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, String> userNicknameMap = UserUtils.getUserNicknameMapByIds(userIdList);
        Map<Long, List<String>> userRoleCodeMap = Maps.newHashMap();
        Boolean isAdmin = UserUtils.isAdmin();
        if (isAdmin) {
            userRoleCodeMap = UserUtils.mapUserRoleCode(userIdList);
        }
        // 获取应用执行人
        Map<Long, List<String>> finalUserRoleCodeMap = userRoleCodeMap;
        List<AppLogConversationInfoRespVO> collect = list.stream()
                .peek(item -> {
                    if (StringUtils.isNotBlank(item.getEndUser())) {
                        item.setAppExecutor(UserUtils.visitorIdentify(item.getEndUser()));
                    } else {
                        item.setAppExecutor(userNicknameMap.get(Long.parseLong(item.getCreator())));
                    }
                    if (isAdmin) {
                        item.setUserLevels(finalUserRoleCodeMap.get(Long.parseLong(item.getCreator())));
                    }
                    // 权益信息
                    StatisticsUserRightReqDTO right = rightMap.get(item.getUid());
                    if (Objects.nonNull(right)) {
                        item.setCostPoints(Optional.ofNullable(right.getMagicBeanCounts()).map(Long::intValue).orElse(item.getCostPoints()));
                        item.setImagePoints(Optional.ofNullable(right.getImageCounts()).map(Long::intValue).orElse(item.getImagePoints()));
                        item.setMatrixPoints(Optional.ofNullable(right.getMatrixBeanCounts()).map(Long::intValue).orElse(null));
                    }

                    // 非管理员，不展示消耗tokens
                    if (!isAdmin) {
                        item.setTokens(null);
                        item.setTotalPrice(null);
                        item.setTotalAnswerTokens(null);
                        item.setTotalMessageTokens(null);
                        item.setCreator(null);
                    }
                })
                .collect(Collectors.toList());
        result.setList(collect);
        return result;
    }

    /**
     * 转换应用执行消息
     *
     * @param message      消息
     * @param conversation 会话
     * @return AppLogMessageRespVO
     */
    private AppLogMessageRespVO transformAppLogMessage(LogAppMessageDO message, LogAppConversationDO conversation, String images) {
        AppLogMessageRespVO appLogMessageResponse = transformAppLogMessage(message, conversation);
        appLogMessageResponse.setImages(AppUtils.split(images));
        appLogMessageResponse.setAiModel(message.getAiModel());
        return appLogMessageResponse;
    }

    /**
     * 转换应用执行消息
     *
     * @param message      消息
     * @param conversation 会话
     * @return AppLogMessageRespVO
     */
    private AppLogMessageRespVO transformAppLogMessage(LogAppMessageDO message, LogAppConversationDO conversation) {
        return transformAppLogMessage(message, Optional.ofNullable(conversation).map(LogAppConversationDO::getAppName).orElse(null));
    }

    /**
     * 转换应用执行消息
     *
     * @param message 消息
     * @param appName 应用名称
     * @return AppLogMessageRespVO
     */
    private AppLogMessageRespVO transformAppLogMessage(LogAppMessageDO message, String appName) {
        AppLogMessageRespVO appLogMessageResponse = new AppLogMessageRespVO();
        appLogMessageResponse.setUid(message.getUid());
        appLogMessageResponse.setConversationUid(message.getAppConversationUid());
        appLogMessageResponse.setAppUid(message.getAppUid());
        appLogMessageResponse.setAppName(appName);
        appLogMessageResponse.setAppMode(message.getAppMode());
        appLogMessageResponse.setFromScene(message.getFromScene());
        appLogMessageResponse.setMessage(message.getMessage());
        appLogMessageResponse.setAnswer(message.getAnswer());
        appLogMessageResponse.setElapsed(message.getElapsed());
        appLogMessageResponse.setStatus(message.getStatus());
        appLogMessageResponse.setTokens(message.getMessageTokens() + message.getAnswerTokens());
        appLogMessageResponse.setPrice(message.getTotalPrice());
        appLogMessageResponse.setCurrency(message.getCurrency());
        appLogMessageResponse.setErrorCode(message.getErrorCode());
        appLogMessageResponse.setErrorMessage(message.getErrorMsg());
        appLogMessageResponse.setCreateTime(message.getCreateTime());
        appLogMessageResponse.setAppInfo(buildAppResponse(message));
        appLogMessageResponse.setMsgType(message.getMsgType());
        appLogMessageResponse.setAiModel(message.getAiModel());
        return appLogMessageResponse;
    }

    /**
     * 转换图片执行消息
     *
     * @param message      消息
     * @param conversation 会话
     * @return ImageLogMessageRespVO
     */
    private ImageLogMessageRespVO transformImageLogMessage(LogAppMessageDO message, LogAppConversationDO conversation) {
        return transformImageLogMessage(message, Optional.ofNullable(conversation).map(LogAppConversationDO::getAppName).orElse(null));
    }

    /**
     * 转换图片执行消息
     *
     * @param message 消息
     * @param appName 会话
     * @return ImageLogMessageRespVO
     */
    private ImageLogMessageRespVO transformImageLogMessage(LogAppMessageDO message, String appName) {
        ImageLogMessageRespVO imageLogMessageResponse = new ImageLogMessageRespVO();
        imageLogMessageResponse.setUid(message.getUid());
        imageLogMessageResponse.setConversationUid(message.getAppConversationUid());
        imageLogMessageResponse.setAppUid(message.getAppUid());
        imageLogMessageResponse.setAppName(appName);
        imageLogMessageResponse.setAppMode(message.getAppMode());
        imageLogMessageResponse.setFromScene(message.getFromScene());
        imageLogMessageResponse.setMessage(message.getMessage());
        imageLogMessageResponse.setElapsed(message.getElapsed());
        imageLogMessageResponse.setStatus(message.getStatus());
        imageLogMessageResponse.setErrorCode(message.getErrorCode());
        imageLogMessageResponse.setErrorMessage(message.getErrorMsg());
        imageLogMessageResponse.setCreateTime(message.getCreateTime());
        imageLogMessageResponse.setImageInfo(transformImageMessageAnswer(message));
        imageLogMessageResponse.setAiModel(message.getAiModel());
        return imageLogMessageResponse;
    }

    /**
     * 转换图片响应消息响应
     *
     * @param message 消息
     * @return ImageMessageRespVO
     */
    private BaseImageResponse transformImageMessageAnswer(LogAppMessageDO message) {
        // 如果没有结果，直接返回 null
        if (StringUtils.isBlank(message.getAnswer())) {
            return BaseImageResponse.ofEmpty(message.getFromScene());
        }
        // 新的数据结构
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(message.getAnswer(), BaseImageResponse.class);
        } catch (Exception exception) {
            log.error("转换图片响应消息响应异常: {}", exception.getMessage());
            return BaseImageResponse.ofEmpty(message.getFromScene());
        }
    }

    /**
     * 构建应用响应
     *
     * @param message 消息
     */
    private AppRespVO buildAppResponse(LogAppMessageDO message) {
        AppRespVO appRespVO = JSONUtil.toBean(message.getAppConfig(), AppRespVO.class);
        if (appRespVO == null) {
            throw ServiceExceptionUtil.exception(new ErrorCode(30000012, "应用配置错误"));
        }

        return appRespVO;
    }

    /**
     * 获取场景列表
     *
     * @param type 类型
     * @return 场景列表
     */
    private List<Option> getSceneOptions(String type) {
        // 生成记录
        if (LogQueryTypeEnum.GENERATE_RECORD.name().equals(type)) {
            if (UserUtils.isAdmin()) {
                return AppSceneEnum.getOptions(AppSceneEnum.ADMIN_SCENES);
            } else {
                return AppSceneEnum.getOptions(AppSceneEnum.GENERATE_RECORD_BASE_SCENES);
            }
        }
        // 应用分析
        if (LogQueryTypeEnum.APP_ANALYSIS.name().equals(type)) {
            List<Option> options = AppSceneEnum.getOptions(AppSceneEnum.APP_ANALYSIS_SCENES);
            if (UserUtils.isAdmin()) {
                AppSceneEnum optimize = AppSceneEnum.OPTIMIZE_PROMPT;
                options.add(Option.of(optimize.name(), optimize.getLabel(), optimize.getLabelEn()));
                AppSceneEnum listing = AppSceneEnum.LISTING_GENERATE;
                options.add(Option.of(listing.name(), listing.getLabel(), listing.getLabelEn()));
                return options;
            }
            return options;
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
     * @return 应用模型列表
     */
    private List<String> getFromSceneList() {
        if (UserUtils.isNotAdmin()) {
            return AppSceneEnum.GENERATE_RECORD_BASE_SCENES.stream().map(AppSceneEnum::name).collect(Collectors.toList());
        }
        return null;
    }
}
