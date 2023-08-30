package com.starcloud.ops.business.app.service.log.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.channel.vo.response.AppPublishChannelRespVO;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.ImageMessageRespVO;
import com.starcloud.ops.business.app.api.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.api.log.vo.response.ImageLogMessageRespVO;
import com.starcloud.ops.business.app.dal.databoject.app.AppDO;
import com.starcloud.ops.business.app.dal.databoject.market.AppMarketDO;
import com.starcloud.ops.business.app.dal.databoject.publish.AppPublishDO;
import com.starcloud.ops.business.app.dal.mysql.app.AppMapper;
import com.starcloud.ops.business.app.dal.mysql.market.AppMarketMapper;
import com.starcloud.ops.business.app.dal.mysql.publish.AppPublishMapper;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.channel.AppPublishChannelService;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.app.util.AppUtils;
import com.starcloud.ops.business.app.util.UserUtils;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageAppUidReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoPageReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationInfoRespVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppMessageStatisticsListVO;
import com.starcloud.ops.business.log.api.message.vo.AppLogMessagePageReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListAppUidReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageStatisticsListReqVO;
import com.starcloud.ops.business.log.convert.LogAppConversationConvert;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationInfoPO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageStatisticsListPO;
import com.starcloud.ops.business.log.enums.ErrorCodeConstants;
import com.starcloud.ops.business.log.enums.LogQueryTypeEnum;
import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import com.starcloud.ops.framework.common.api.dto.Option;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.APP_MARKET_NO_EXISTS_UID;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.APP_NO_EXISTS_UID;
import static com.starcloud.ops.business.app.enums.ErrorCodeConstants.APP_PUBLISH_NOT_EXISTS_UID;

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
    private LogAppConversationService logAppConversationService;

    @Resource
    private ChatService chatService;

    @Resource
    private AppPublishChannelService appPublishChannelService;

    @Resource
    private AppMapper appMapper;

    @Resource
    private AppPublishMapper appPublishMapper;

    @Resource
    private AppMarketMapper appMarketMapper;

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
     * 根据 应用 UID 获取应用执行日志消息统计数据列表 <br>
     * 1. 应用分析 <br>
     * 2. 聊天分析 <br>
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    @Override
    public List<LogAppMessageStatisticsListVO> listLogMessageStatisticsByAppUid(LogAppMessageStatisticsListAppUidReqVO query) {
        // 应用 UID 不能为空
        AppValidate.notBlank(query.getAppUid(), new ErrorCode(3000001, "应用分析时，应用UID[appUid]为必填项"));
        // 查询应用类型
        AppDO app = appMapper.get(query.getAppUid(), Boolean.TRUE);
        AppValidate.notNull(app, APP_NO_EXISTS_UID, query.getAppUid());

        // 应用模型为 COMPLETION 时，说明为应用场景下的应用分析
        if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {
            if (StringUtils.isNotBlank(query.getFromScene()) && !AppSceneEnum.APP_ANALYSIS_SCENES_NAME.contains(query.getFromScene())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(3000001, "应用分析时，应用场景[fromScene]不正确，支持的场景为：" + AppSceneEnum.APP_ANALYSIS_SCENES_NAME));
            }
        }

        // 应用模型为 CHAT 时，说明为聊天场景下的应用分析
        if (AppModelEnum.CHAT.name().equals(app.getModel())) {
            if (StringUtils.isNotBlank(query.getFromScene()) && !AppSceneEnum.CHAT_ANALYSIS_SCENES_NAME.contains(query.getFromScene())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(3000001, "聊天应用分析时，应用场景[fromScene]不正确，支持的场景为：" + AppSceneEnum.CHAT_ANALYSIS_SCENES_NAME));
            }
        }

        // 执行场景不为空的情况
        if (StringUtils.isNotBlank(query.getFromScene())) {
            // 如果执行场景不是 WEB_MARKET，则不需要查询应用市场执行信息。如果是 WEB_MARKET，则需要查询应用市场执行信息。
            if (!AppSceneEnum.WEB_MARKET.name().equals(query.getFromScene())) {
                query.setMarketUid(null);
            } else {
                String marketUid = getMarketUidByApp(app);
                // 未获取到应用市场 UID，则直接返回空数据。不需要再走数据库查询
                if (StringUtils.isBlank(marketUid)) {
                    return Collections.emptyList();
                }
                query.setMarketUid(marketUid);
            }
        } else {
            // 执行场景为空的情况，需要查询应用市场执行信息。
            query.setMarketUid(getMarketUidByApp(app));
        }

        // 时间类型默认值
        query.setTimeType(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType());
        List<LogAppMessageStatisticsListPO> pageResult = logAppConversationService.listLogMessageStatisticsByAppUid(query);
        return LogAppConversationConvert.INSTANCE.convertStatisticsList(pageResult);
    }

    private String getMarketUidByApp(AppDO app) {
        // 如果 publishUid 为空，说明此应用未发布成功到应用市场过，直接返回空数据
        String publishUid = app.getPublishUid();
        if (StringUtils.isBlank(publishUid)) {
            return null;
        }

        // 查询发布信息并校验是否存在
        String appPublishUid = AppUtils.obtainUid(publishUid);
        AppPublishDO appPublish = appPublishMapper.get(appPublishUid, Boolean.TRUE);
        AppValidate.notNull(appPublish, APP_PUBLISH_NOT_EXISTS_UID, appPublishUid);

        // marketUid 为空，说明可能数据有问题，直接返回空数据
        String marketUid = appPublish.getMarketUid();
        if (StringUtils.isBlank(marketUid)) {
            return null;
        }

        // 查询应用市场信息并校验是否存在
        AppMarketDO appMarket = appMarketMapper.get(marketUid, Boolean.TRUE);
        AppValidate.notNull(appMarket, APP_MARKET_NO_EXISTS_UID, marketUid);

        return appMarket.getUid();
    }

    /**
     * 获取应用执行日志消息统计数据
     *
     * @param query 查询条件
     * @return 日志消息统计数据
     */
    @Override
    public List<LogAppMessageStatisticsListVO> listLogMessageStatistics(LogAppMessageStatisticsListReqVO query) {
        if (StringUtils.isBlank(query.getFromScene())) {
            query.setFromSceneList(getFromSceneList());
        }
        // 时间类型默认值
        query.setTimeType(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType());
        List<LogAppMessageStatisticsListPO> pageResult = logAppConversationService.listLogMessageStatistics(query);
        return LogAppConversationConvert.INSTANCE.convertStatisticsList(pageResult);
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
    public PageResult<LogAppConversationInfoRespVO> pageLogConversationByAppUid(LogAppConversationInfoPageAppUidReqVO query) {
        // 应用 UID 不能为空
        AppValidate.notBlank(query.getAppUid(), new ErrorCode(3000001, "应用分析时，应用UID[appUid]为必填项"));
        // 查询应用类型
        AppDO app = appMapper.get(query.getAppUid(), Boolean.TRUE);
        AppValidate.notNull(app, APP_NO_EXISTS_UID, query.getAppUid());

        // 应用模型为 COMPLETION 时，说明为应用场景下的应用分析
        if (AppModelEnum.COMPLETION.name().equals(app.getModel())) {
            if (StringUtils.isNotBlank(query.getFromScene()) && !AppSceneEnum.APP_ANALYSIS_SCENES_NAME.contains(query.getFromScene())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(3000001, "应用分析时，应用场景[fromScene]不正确，支持的场景为：" + AppSceneEnum.APP_ANALYSIS_SCENES_NAME));
            }
        }

        // 应用模型为 CHAT 时，说明为聊天场景下的应用分析
        if (AppModelEnum.CHAT.name().equals(app.getModel())) {
            if (StringUtils.isNotBlank(query.getFromScene()) && !AppSceneEnum.CHAT_ANALYSIS_SCENES_NAME.contains(query.getFromScene())) {
                throw ServiceExceptionUtil.exception(new ErrorCode(3000001, "聊天应用分析时，应用场景[fromScene]不正确，支持的场景为：" + AppSceneEnum.CHAT_ANALYSIS_SCENES_NAME));
            }
        }

        // 执行场景不为空的情况
        if (StringUtils.isNotBlank(query.getFromScene())) {
            // 如果执行场景不是 WEB_MARKET，则不需要查询应用市场执行信息。如果是 WEB_MARKET，则需要查询应用市场执行信息。
            if (!AppSceneEnum.WEB_MARKET.name().equals(query.getFromScene())) {
                query.setMarketUid(null);
            } else {
                String marketUid = getMarketUidByApp(app);
                // 未获取到应用市场 UID，则直接返回空数据。不需要再走数据库查询
                if (StringUtils.isBlank(marketUid)) {
                    return new PageResult<>(Collections.emptyList(), 0L);
                }
                query.setMarketUid(marketUid);
            }
        } else {
            // 执行场景为空的情况，需要查询应用市场执行信息。
            query.setMarketUid(getMarketUidByApp(app));
        }

        // 时间类型默认值
        query.setTimeType(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType());
        PageResult<LogAppConversationInfoPO> pageResult = logAppConversationService.pageLogConversationByAppUid(query);
        PageResult<LogAppConversationInfoRespVO> result = LogAppConversationConvert.INSTANCE.convertInfoPage(pageResult);
        List<LogAppConversationInfoRespVO> list = result.getList();
        List<LogAppConversationInfoRespVO> collect = CollectionUtil.emptyIfNull(list).stream()
                .peek(item -> item.setAppExecutor(UserUtils.identify(item.getCreator(), item.getEndUser())))
                .collect(Collectors.toList());
        result.setList(collect);
        return result;
    }

    /**
     * 分页查询应用执行日志会话数据
     *
     * @param query 查询条件
     * @return 应用执行日志会话数据
     */
    @Override
    @DataPermission
    public PageResult<LogAppConversationInfoRespVO> pageLogConversation(LogAppConversationInfoPageReqVO query) {
        if (StringUtils.isBlank(query.getFromScene())) {
            query.setFromSceneList(getFromSceneList());
        }
        // 时间类型默认值
        query.setTimeType(StringUtils.isBlank(query.getTimeType()) ? LogTimeTypeEnum.ALL.name() : query.getTimeType());
        PageResult<LogAppConversationInfoPO> pageResult = logAppConversationService.pageLogConversation(query);
        PageResult<LogAppConversationInfoRespVO> result = LogAppConversationConvert.INSTANCE.convertInfoPage(pageResult);
        List<LogAppConversationInfoRespVO> list = result.getList();
        List<LogAppConversationInfoRespVO> collect = CollectionUtil.emptyIfNull(list).stream()
                .peek(item -> item.setAppExecutor(UserUtils.identify(item.getCreator(), item.getEndUser())))
                .collect(Collectors.toList());
        result.setList(collect);
        return result;
    }

    /**
     * 获取文本生成消息详情
     *
     * @param query 查询条件
     * @return AppLogMessageRespVO
     */
    @Override
    public AppLogMessageRespVO getLogAppMessageDetail(AppLogMessagePageReqVO query) {
        // 获取会话记录
        LogAppConversationDO appConversation = logAppConversationService.getAppConversation(query.getConversationUid());
        AppValidate.notNull(appConversation, ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS_UID, query.getConversationUid());

        // 获取消息列表
        Page<LogAppMessageDO> appMessagePage = logAppMessageService.getAppMessageList(query);
        List<LogAppMessageDO> appMessageList = appMessagePage.getRecords();
        // 校验日志消息是否存在
        AppValidate.notEmpty(appMessageList, ErrorCodeConstants.APP_MESSAGE_NOT_EXISTS);

        // 不管是多步骤执行，还是单步骤执行，都是取最新一条记录转换返回。
        LogAppMessageDO logAppMessage = appMessageList.get(0);
        AppLogMessageRespVO appLogMessageResponse = transformAppLogMessage(logAppMessage, appConversation);

        // 如果是通过渠道执行的，需要查询渠道信息
        if (StringUtils.isNotBlank(logAppMessage.getMediumUid())) {
            AppPublishChannelRespVO channel = appPublishChannelService.getByMediumUid(logAppMessage.getMediumUid());
            appLogMessageResponse.setChannel(channel);
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
    public PageResult<AppLogMessageRespVO> getChatMessageDetail(AppLogMessagePageReqVO query) {
        LogAppConversationDO appConversation = logAppConversationService.getAppConversation(query.getConversationUid());
        AppValidate.notNull(appConversation, ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS_UID, query.getConversationUid());
        String images = StringUtils.EMPTY;

        if (AppSceneEnum.CHAT_MARKET.name().equals(query.getFromScene())) {
            AppMarketDO appMarketDO = appMarketMapper.get(appConversation.getAppUid(), Boolean.TRUE);
            AppValidate.notNull(appMarketDO, APP_NO_EXISTS_UID, appConversation.getAppUid());
            images = appMarketDO.getImages();
        } else {
            AppDO app = appMapper.get(appConversation.getAppUid(), Boolean.TRUE);
            AppValidate.notNull(app, APP_NO_EXISTS_UID, appConversation.getAppUid());
            images = app.getImages();
        }

        PageResult<LogAppMessageDO> messagePageResult = chatService.chatHistory(query.getConversationUid(), query.getPageNo(), query.getPageSize());
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
    public ImageLogMessageRespVO getLogImageMessageDetail(AppLogMessagePageReqVO query) {
        // 获取会话
        LogAppConversationDO appConversation = logAppConversationService.getAppConversation(query.getConversationUid());
        AppValidate.notNull(appConversation, ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS_UID, query.getConversationUid());

        // 获取消息列表
        Page<LogAppMessageDO> appMessagePage = logAppMessageService.getAppMessageList(query);
        List<LogAppMessageDO> appMessageList = appMessagePage.getRecords();
        // 校验日志消息是否存在
        AppValidate.notEmpty(appMessageList, ErrorCodeConstants.APP_MESSAGE_NOT_EXISTS);

        // 取第一条数据
        LogAppMessageDO logAppMessage = appMessageList.get(0);
        ImageLogMessageRespVO imageLogMessageResponse = transformImageLogMessage(logAppMessage, appConversation);

        // 如果是通过渠道执行的，需要查询渠道信息
        if (StringUtils.isNotBlank(logAppMessage.getMediumUid())) {
            AppPublishChannelRespVO channel = appPublishChannelService.getByMediumUid(logAppMessage.getMediumUid());
            imageLogMessageResponse.setChannel(channel);
        }

        return imageLogMessageResponse;
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
        AppLogMessageRespVO appLogMessageResponse = new AppLogMessageRespVO();
        appLogMessageResponse.setUid(message.getUid());
        appLogMessageResponse.setConversationUid(message.getAppConversationUid());
        appLogMessageResponse.setAppUid(message.getAppUid());
        appLogMessageResponse.setAppName(conversation.getAppName());
        appLogMessageResponse.setAppMode(message.getAppMode());
        appLogMessageResponse.setFromScene(message.getFromScene());
        appLogMessageResponse.setMessage(message.getMessage());
        appLogMessageResponse.setAnswer(message.getAnswer());
        appLogMessageResponse.setElapsed(message.getElapsed());
        appLogMessageResponse.setStatus(message.getStatus());
        appLogMessageResponse.setTokens(message.getMessageTokens() + message.getAnswerTokens());
        appLogMessageResponse.setPrice(message.getTotalPrice());
        appLogMessageResponse.setCurrency(message.getCurrency());
        appLogMessageResponse.setAppExecutor(UserUtils.identify(message.getCreator(), message.getEndUser()));
        appLogMessageResponse.setErrorCode(message.getErrorCode());
        appLogMessageResponse.setErrorMessage(message.getErrorMsg());
        appLogMessageResponse.setCreateTime(message.getCreateTime());
        appLogMessageResponse.setAppInfo(buildAppResponse(message));
        appLogMessageResponse.setMsgType(message.getMsgType());
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
        ImageLogMessageRespVO imageLogMessageResponse = new ImageLogMessageRespVO();
        imageLogMessageResponse.setUid(message.getUid());
        imageLogMessageResponse.setConversationUid(message.getAppConversationUid());
        imageLogMessageResponse.setAppUid(message.getAppUid());
        imageLogMessageResponse.setAppName(conversation.getAppName());
        imageLogMessageResponse.setAppMode(message.getAppMode());
        imageLogMessageResponse.setFromScene(message.getFromScene());
        imageLogMessageResponse.setMessage(message.getMessage());
        imageLogMessageResponse.setElapsed(message.getElapsed());
        imageLogMessageResponse.setStatus(message.getStatus());
        imageLogMessageResponse.setErrorCode(message.getErrorCode());
        imageLogMessageResponse.setAppExecutor(UserUtils.identify(message.getCreator(), message.getEndUser()));
        imageLogMessageResponse.setErrorMessage(message.getErrorMsg());
        imageLogMessageResponse.setCreateTime(message.getCreateTime());
        imageLogMessageResponse.setImageInfo(transformImageMessage(message));
        return imageLogMessageResponse;
    }

    /**
     * 转换图片响应消息响应
     *
     * @param message 消息
     * @return ImageMessageRespVO
     */
    private ImageMessageRespVO transformImageMessage(LogAppMessageDO message) {
        // 如果没有结果，直接返回 null
        if (StringUtils.isBlank(message.getAnswer())) {
            return null;
        }

        // 获取图片列表
        TypeReference<List<ImageDTO>> typeReference = new TypeReference<List<ImageDTO>>() {
        };
        List<ImageDTO> imageList = CollectionUtil.emptyIfNull(JSONUtil.toBean(message.getAnswer(), typeReference, Boolean.TRUE)).stream()
                .filter(imageItem -> Objects.nonNull(imageItem) && StringUtils.isNotBlank(imageItem.getUrl()))
                .collect(Collectors.toList());

        // 如果没有结果，返回 null
        if (CollectionUtil.isEmpty(imageList)) {
            return null;
        }

        // 构建图片响应
        ImageMessageRespVO imageResponse = new ImageMessageRespVO();
        imageResponse.setPrompt(message.getMessage());
        imageResponse.setCreateTime(message.getCreateTime());
        imageResponse.setImages(imageList);
        ImageRequest imageRequest = JSONUtil.toBean(message.getAppConfig(), ImageRequest.class);
        if (imageRequest != null) {
            imageResponse.setNegativePrompt(ImageUtils.handleNegativePrompt(imageRequest.getNegativePrompt(), Boolean.FALSE));
            imageResponse.setEngine(imageRequest.getEngine());
            imageResponse.setWidth(imageRequest.getWidth());
            imageResponse.setHeight(imageRequest.getHeight());
            imageResponse.setSteps(imageRequest.getSteps());
        }

        return imageResponse;
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
            String permission = UserUtils.getDeptDataPermission();
            if (UserUtils.ALL.equals(permission)) {
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
     * @return 应用模型列表
     */
    private List<String> getFromSceneList() {
        String permission = UserUtils.getDeptDataPermission();
        if (!UserUtils.ALL.equals(permission)) {
            return AppSceneEnum.GENERATE_RECORD_BASE_SCENES.stream().map(AppSceneEnum::name).collect(Collectors.toList());
        }
        return null;
    }
}
