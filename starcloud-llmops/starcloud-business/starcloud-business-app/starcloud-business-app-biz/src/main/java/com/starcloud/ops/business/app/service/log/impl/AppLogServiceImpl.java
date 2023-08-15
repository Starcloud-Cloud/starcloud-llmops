package com.starcloud.ops.business.app.service.log.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.ImageMessageRespVO;
import com.starcloud.ops.business.app.api.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.api.log.vo.response.ImageLogMessageRespVO;
import com.starcloud.ops.business.app.service.app.AppService;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.app.util.IdentifyUserUtils;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.app.validate.AppValidate;
import com.starcloud.ops.business.log.api.message.vo.AppLogMessagePageReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.ErrorCodeConstants;
import com.starcloud.ops.business.log.service.conversation.LogAppConversationService;
import com.starcloud.ops.business.log.service.message.LogAppMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-08-09
 */
@Slf4j
@Service
public class AppLogServiceImpl implements AppLogService {

    @Resource
    private LogAppMessageService logAppMessageService;

    @Resource
    private LogAppConversationService logAppConversationService;

    @Resource
    private AppService appService;

    @Resource
    private ChatService chatService;

    @Resource
    private IdentifyUserUtils identifyUserUtils;

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
        return transformAppLogMessage(appMessageList.get(0), appConversation);
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
        if (Objects.isNull(appConversation)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS_UID, query.getConversationUid());
        }

        AppRespVO appRespVO = appService.get(appConversation.getAppUid());
        PageResult<LogAppMessageDO> messageDOPageResult = chatService.chatHistory(query.getConversationUid(), query.getPageNo(), query.getPageSize());
        List<AppLogMessageRespVO> collect = CollectionUtil.emptyIfNull(messageDOPageResult.getList()).stream().filter(Objects::nonNull)
                .map(item -> {
                    AppLogMessageRespVO appLogMessageRespVO = new AppLogMessageRespVO();
                    appLogMessageRespVO.setUid(item.getUid());
                    appLogMessageRespVO.setConversationUid(item.getAppConversationUid());
                    appLogMessageRespVO.setAppUid(item.getAppUid());
                    appLogMessageRespVO.setAppName(appConversation.getAppName());
                    appLogMessageRespVO.setAppMode(item.getAppMode());
                    appLogMessageRespVO.setFromScene(item.getFromScene());
                    appLogMessageRespVO.setMessage(item.getMessage());
                    appLogMessageRespVO.setAnswer(item.getAnswer());
                    appLogMessageRespVO.setElapsed(item.getElapsed());
                    appLogMessageRespVO.setStatus(item.getStatus());
                    appLogMessageRespVO.setTokens(item.getMessageTokens() + item.getAnswerTokens());
                    appLogMessageRespVO.setPrice(item.getTotalPrice());
                    appLogMessageRespVO.setCurrency(item.getCurrency());
                    appLogMessageRespVO.setErrorCode(item.getErrorCode());
                    appLogMessageRespVO.setAppExecutor(identifyUserUtils.identifyUser(item.getCreator(), item.getEndUser()));
                    appLogMessageRespVO.setErrorMessage(item.getErrorMsg());
                    appLogMessageRespVO.setCreateTime(item.getCreateTime());
                    appLogMessageRespVO.setImages(appRespVO.getImages());
                    appLogMessageRespVO.setAppInfo(JSONUtil.toBean(item.getAppConfig(), AppRespVO.class));
                    return appLogMessageRespVO;
                }).collect(Collectors.toList());
        return new PageResult<>(collect, messageDOPageResult.getTotal());
    }

    /**
     * 获取图片生成消息详情
     *
     * @param query 查询条件
     * @return ImageRespVO
     */
    @Override
    public PageResult<ImageLogMessageRespVO> getLogImageMessageDetail(AppLogMessagePageReqVO query) {
        // 获取会话
        LogAppConversationDO appConversation = logAppConversationService.getAppConversation(query.getConversationUid());
        AppValidate.notNull(appConversation, ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS_UID, query.getConversationUid());

        // 获取消息列表
        Page<LogAppMessageDO> appMessagePage = logAppMessageService.getAppMessageList(query);
        List<LogAppMessageDO> appMessageList = appMessagePage.getRecords();
        // 校验日志消息是否存在
        AppValidate.notEmpty(appMessageList, ErrorCodeConstants.APP_MESSAGE_NOT_EXISTS);

        // 处理图片消息数据
        List<ImageLogMessageRespVO> collect = CollectionUtil.emptyIfNull(appMessageList).stream()
                .filter(Objects::nonNull)
                .map(item -> transformImageLogMessage(item, appConversation))
                .collect(Collectors.toList());

        return new PageResult<>(collect, appMessagePage.getTotal());
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
        appLogMessageResponse.setAppExecutor(identifyUserUtils.identifyUser(message.getCreator(), message.getEndUser()));
        appLogMessageResponse.setErrorCode(message.getErrorCode());
        appLogMessageResponse.setErrorMessage(message.getErrorMsg());
        appLogMessageResponse.setCreateTime(message.getCreateTime());
        appLogMessageResponse.setAppInfo(buildAppResponse(message));
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
        imageLogMessageResponse.setAppExecutor(identifyUserUtils.identifyUser(message.getCreator(), message.getEndUser()));
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
}
