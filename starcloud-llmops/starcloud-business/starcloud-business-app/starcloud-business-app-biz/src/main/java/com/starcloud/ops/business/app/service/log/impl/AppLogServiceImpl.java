package com.starcloud.ops.business.app.service.log.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.ImageMessageRespVO;
import com.starcloud.ops.business.app.api.log.vo.response.AppLogMessageRespVO;
import com.starcloud.ops.business.app.api.log.vo.response.ImageLogMessageRespVO;
import com.starcloud.ops.business.app.service.chat.ChatService;
import com.starcloud.ops.business.app.service.log.AppLogService;
import com.starcloud.ops.business.app.util.ImageUtils;
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
    private ChatService chatService;

    /**
     * 获取文本生成消息详情
     *
     * @param query 查询条件
     * @return AppLogMessageRespVO
     */
    @Override
    public PageResult<AppLogMessageRespVO> getLogAppMessageDetail(AppLogMessagePageReqVO query) {
        // 获取会话
        LogAppConversationDO appConversation = logAppConversationService.getAppConversation(query.getConversationUid());
        if (Objects.isNull(appConversation)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS_UID, query.getConversationUid());
        }

        // 获取消息列表
        Page<LogAppMessageDO> appMessagePage = logAppMessageService.getAppMessageList(query);
        List<LogAppMessageDO> appMessageList = appMessagePage.getRecords();

        // 处理信息数据
        List<AppLogMessageRespVO> collect = CollectionUtil.emptyIfNull(appMessageList).stream().filter(Objects::nonNull)
                .map(item -> {
                    AppLogMessageRespVO appLogMessageRespVO = new AppLogMessageRespVO();
                    appLogMessageRespVO.setUid(item.getUid());
                    appLogMessageRespVO.setConversationUid(item.getAppConversationUid());
                    appLogMessageRespVO.setAppUid(item.getAppUid());
                    appLogMessageRespVO.setAppName(appConversation.getAppName());
                    appLogMessageRespVO.setAppMode(item.getAppMode());
                    appLogMessageRespVO.setFromScene(item.getFromScene());
                    appLogMessageRespVO.setMessage(item.getMessage());
                    appLogMessageRespVO.setElapsed(item.getElapsed());
                    appLogMessageRespVO.setStatus(item.getStatus());
                    appLogMessageRespVO.setTokens(item.getMessageTokens() + item.getAnswerTokens());
                    appLogMessageRespVO.setPrice(item.getTotalPrice());
                    appLogMessageRespVO.setCurrency(item.getCurrency());
                    appLogMessageRespVO.setErrorCode(item.getErrorCode());
                    appLogMessageRespVO.setEndUser(identifyUser(item.getEndUser()));
                    appLogMessageRespVO.setErrorMessage(item.getErrorMsg());
                    appLogMessageRespVO.setCreateTime(item.getCreateTime());
                    appLogMessageRespVO.setAppInfo(JSONUtil.toBean(item.getAppConfig(), AppRespVO.class));
                    return appLogMessageRespVO;
                }).collect(Collectors.toList());

        return new PageResult<>(collect, appMessagePage.getTotal());
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
        if (Objects.isNull(appConversation)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS_UID, query.getConversationUid());
        }

        // 获取消息列表
        Page<LogAppMessageDO> appMessagePage = logAppMessageService.getAppMessageList(query);
        List<LogAppMessageDO> appMessageList = appMessagePage.getRecords();

        // 处理图片消息数据
        List<ImageLogMessageRespVO> collect = CollectionUtil.emptyIfNull(appMessageList).stream().map(item -> {
            ImageLogMessageRespVO imageLogMessageRespVO = new ImageLogMessageRespVO();
            imageLogMessageRespVO.setUid(item.getUid());
            imageLogMessageRespVO.setConversationUid(item.getAppConversationUid());
            imageLogMessageRespVO.setAppUid(item.getAppUid());
            imageLogMessageRespVO.setAppName(appConversation.getAppName());
            imageLogMessageRespVO.setAppMode(item.getAppMode());
            imageLogMessageRespVO.setFromScene(item.getFromScene());
            imageLogMessageRespVO.setMessage(item.getMessage());
            imageLogMessageRespVO.setElapsed(item.getElapsed());
            imageLogMessageRespVO.setStatus(item.getStatus());
            imageLogMessageRespVO.setErrorCode(item.getErrorCode());
            imageLogMessageRespVO.setEndUser(identifyUser(item.getEndUser()));
            imageLogMessageRespVO.setErrorMessage(item.getErrorMsg());
            imageLogMessageRespVO.setCreateTime(item.getCreateTime());

            // 如果没有结果，返回 null
            if (StringUtils.isBlank(item.getAnswer())) {
                return null;
            }
            List<ImageDTO> imageList = JSONUtil.toBean(item.getAnswer(), new TypeReference<List<ImageDTO>>() {
            }, true);
            // 排除掉空的和没有 url 的图片
            imageList = imageList.stream().filter(Objects::nonNull).filter(imageItem -> StringUtils.isNotBlank(imageItem.getUrl())).collect(Collectors.toList());
            // 如果没有结果，返回 null
            if (CollectionUtil.isEmpty(imageList)) {
                return null;
            }
            ImageMessageRespVO imageResponse = new ImageMessageRespVO();
            imageResponse.setPrompt(item.getMessage());
            imageResponse.setCreateTime(item.getCreateTime());
            imageResponse.setImages(imageList);
            ImageRequest imageRequest = JSONUtil.toBean(item.getAppConfig(), ImageRequest.class);
            if (imageRequest != null) {
                imageResponse.setNegativePrompt(ImageUtils.handleNegativePrompt(imageRequest.getNegativePrompt(), Boolean.FALSE));
                imageResponse.setEngine(imageRequest.getEngine());
                imageResponse.setWidth(imageRequest.getWidth());
                imageResponse.setHeight(imageRequest.getHeight());
                imageResponse.setSteps(imageRequest.getSteps());
            }
            imageLogMessageRespVO.setImageInfo(imageResponse);
            return imageLogMessageRespVO;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return new PageResult<>(collect, appMessagePage.getTotal());
    }

    @Override
    public PageResult<AppLogMessageRespVO> getChatMessageDetail(AppLogMessagePageReqVO query) {
        LogAppConversationDO appConversation = logAppConversationService.getAppConversation(query.getConversationUid());
        if (Objects.isNull(appConversation)) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_CONVERSATION_NOT_EXISTS_UID, query.getConversationUid());
        }

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
                    appLogMessageRespVO.setEndUser(identifyUser(item.getEndUser()));
                    appLogMessageRespVO.setErrorMessage(item.getErrorMsg());
                    appLogMessageRespVO.setCreateTime(item.getCreateTime());
                    appLogMessageRespVO.setAppInfo(JSONUtil.toBean(item.getAppConfig(), AppRespVO.class));
                    return appLogMessageRespVO;
                }).collect(Collectors.toList());
        return new PageResult<>(collect, messageDOPageResult.getTotal());
    }

    /**
     * 获取用户或者游客
     *
     * @param input 用户输入
     * @return 用户或者游客
     */
    public static String identifyUser(String input) {
        try {
            Long.parseLong(input);
            return "用户";
        } catch (NumberFormatException e) {
            return "游客";
        }
    }
}
