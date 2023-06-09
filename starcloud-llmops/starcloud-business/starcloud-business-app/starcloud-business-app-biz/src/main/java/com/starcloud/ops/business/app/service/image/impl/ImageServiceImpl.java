package com.starcloud.ops.business.app.service.image.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.dto.ImageMetaDTO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageReqVO;
import com.starcloud.ops.business.app.api.image.vo.response.ImageMessageRespVO;
import com.starcloud.ops.business.app.api.image.vo.response.ImageRespVO;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.image.ImageService;
import com.starcloud.ops.business.app.service.image.VSearchImageService;
import com.starcloud.ops.business.app.util.ImageMetaUtil;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.dal.mysql.LogAppConversationMapper;
import com.starcloud.ops.business.log.dal.mysql.LogAppMessageMapper;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-07-12
 */
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    @Resource
    private UserBenefitsService benefitsService;

    @Resource
    private LogAppConversationMapper logAppConversationMapper;

    @Resource
    private LogAppMessageMapper logAppMessageMapper;

    @Resource
    private VSearchImageService vSearchImageService;

    /**
     * 获取图片元数据
     *
     * @return 图片元数据
     */
    @Override
    public Map<String, List<ImageMetaDTO>> meta() {
        Map<String, List<ImageMetaDTO>> meta = new HashMap<>(8);
        meta.put("samples", ImageMetaUtil.samplesList());
        meta.put("imageSize", ImageMetaUtil.imageSizeList());
        meta.put("sampler", ImageMetaUtil.samplerList());
        meta.put("guidancePreset", ImageMetaUtil.guidancePresetList());
        meta.put("stylePreset", ImageMetaUtil.stylePresetList());
        return meta;
    }

    /**
     * 查询历史图片列表
     *
     * @return 图片列表
     */
    @Override
    public ImageRespVO historyGenerateImages() {
        ImageRespVO response = new ImageRespVO();
        Long loginUserId = WebFrameworkUtils.getLoginUserId();
        LogAppConversationDO conversation = this.getConversation(null, loginUserId);
        response.setConversationId(conversation.getUid());
        // 查询会话下的消息
        LambdaQueryWrapper<LogAppMessageDO> messageWrapper = Wrappers.lambdaQuery(LogAppMessageDO.class);
        messageWrapper.select(LogAppMessageDO::getUid, LogAppMessageDO::getCreateTime, LogAppMessageDO::getMessage, LogAppMessageDO::getAnswer);
        messageWrapper.eq(LogAppMessageDO::getAppConversationUid, conversation.getUid());
        messageWrapper.eq(LogAppMessageDO::getAppMode, AppModelEnum.BASE_GENERATE_IMAGE.name());
        messageWrapper.eq(LogAppMessageDO::getCreator, Long.toString(WebFrameworkUtils.getLoginUserId()));
        messageWrapper.eq(LogAppMessageDO::getStatus, LogStatusEnum.SUCCESS.name());
        messageWrapper.eq(LogAppMessageDO::getDeleted, Boolean.FALSE);
        messageWrapper.orderByDesc(LogAppMessageDO::getCreateTime);
        List<LogAppMessageDO> messageList = logAppMessageMapper.selectList(messageWrapper);
        // 处理图片消息数据
        List<ImageMessageRespVO> list = CollectionUtil.emptyIfNull(messageList).stream().map(item -> {
            // 如果没有结果，返回 null
            if (StringUtils.isBlank(item.getAnswer())) {
                return null;
            }
            List<ImageDTO> imageList = JSON.parseArray(item.getAnswer(), ImageDTO.class);
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
            return imageResponse;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        response.setMessages(list);
        return response;
    }

    /**
     * 文字生成图片
     *
     * @param request 请求参数
     * @return 图片列表
     */
    @Override
    public ImageMessageRespVO textToImage(ImageReqVO request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Text to Image Task");
        Long userId = WebFrameworkUtils.getLoginUserId();
        // 会话记录
        LogAppConversationDO conversation = this.getConversation(request.getConversationId(), userId);
        try {
            // 检测权益
            benefitsService.allowExpendBenefits(BenefitsTypeEnums.IMAGE.getCode(), userId);
            // 调用图片生成服务
            List<ImageDTO> imageList = vSearchImageService.textToImage(request.getImageRequest());
            // 扣除权益
            benefitsService.expendBenefits(BenefitsTypeEnums.IMAGE.getCode(), (long) imageList.size(), userId, conversation.getUid());
            // 消息记录
            stopWatch.stop();
            LogAppMessageDO messageRequest = buildAppMessageLog(request, conversation, userId);
            messageRequest.setStatus(LogStatusEnum.SUCCESS.name());
            messageRequest.setAnswer(JSON.toJSONString(imageList));
            messageRequest.setAnswerTokens(imageList.size());
            messageRequest.setMessageUnitPrice(new BigDecimal("0"));
            messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
            messageRequest.setTotalPrice(new BigDecimal(Integer.toString(imageList.size())));
            logAppMessageMapper.insert(messageRequest);
            // 更新会话记录
            this.updateAppConversation(conversation.getUid(), LogStatusEnum.SUCCESS, request);
            ImageMessageRespVO imageResponse = new ImageMessageRespVO();
            imageResponse.setPrompt(request.getImageRequest().getPrompt());
            imageResponse.setCreateTime(LocalDateTime.now());
            imageResponse.setImages(imageList);
            return imageResponse;
        } catch (ServiceException e) {
            stopWatch.stop();
            // 消息记录
            LogAppMessageDO messageRequest = buildAppMessageLog(request, conversation, userId);
            messageRequest.setStatus(LogStatusEnum.ERROR.name());
            messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
            messageRequest.setErrorCode(Integer.toString(e.getCode()));
            messageRequest.setErrorMsg(e.getMessage());
            logAppMessageMapper.insert(messageRequest);
            // 更新会话记录
            this.updateAppConversation(conversation.getUid(), LogStatusEnum.ERROR, request);
            log.error("文字生成图片失败，错误码：{}, 错误信息：{}", e.getCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            stopWatch.stop();
            // 消息记录
            LogAppMessageDO messageRequest = buildAppMessageLog(request, conversation, userId);
            messageRequest.setStatus(LogStatusEnum.ERROR.name());
            messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
            messageRequest.setErrorCode("300300000");
            messageRequest.setErrorMsg(e.getMessage());
            logAppMessageMapper.insert(messageRequest);
            // 更新会话记录
            this.updateAppConversation(conversation.getUid(), LogStatusEnum.ERROR, request);
            log.error("文字生成图片失败，错误码：{}, 错误信息：{}", messageRequest.getErrorCode(), e.getMessage(), e);
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), e.getMessage());
        }
    }

    /**
     * 获取会话记录 <br>
     * 1. 如果会话记录id不为空，则根据会话记录id查询 <br>
     * 2. 如果会话记录id为空，则根据用户id查询最新的一条会话记录
     * 3. 如果没有会话记录，则创建一条会话记录
     *
     * @param conversationUid 会话记录id
     * @param userId          用户id
     * @return 会话记录
     */
    private LogAppConversationDO getConversation(String conversationUid, Long userId) {
        LambdaQueryWrapper<LogAppConversationDO> conversationWrapper = Wrappers.lambdaQuery(LogAppConversationDO.class);
        conversationWrapper.eq(LogAppConversationDO::getAppMode, AppModelEnum.BASE_GENERATE_IMAGE.name());
        conversationWrapper.eq(LogAppConversationDO::getCreator, Long.toString(userId));
        conversationWrapper.eq(LogAppConversationDO::getDeleted, Boolean.FALSE);
        if (StringUtils.isNotBlank(conversationUid)) {
            conversationWrapper.eq(LogAppConversationDO::getUid, conversationUid);
        } else {
            conversationWrapper.orderByDesc(LogAppConversationDO::getCreateTime);
            conversationWrapper.last("limit 1");
        }
        LogAppConversationDO conversation = logAppConversationMapper.selectOne(conversationWrapper);
        if (conversation != null) {
            return conversation;
        }

        // 新增一条会话记录
        conversation = new LogAppConversationDO();
        String fastConversationUid = IdUtil.fastSimpleUUID();
        conversation.setUid(fastConversationUid);
        conversation.setAppMode(AppModelEnum.BASE_GENERATE_IMAGE.name());
        conversation.setAppName("基础生成图片应用：文本生成图片，图片生成图片");
        conversation.setStatus(LogStatusEnum.ERROR.name());
        conversation.setAppUid(fastConversationUid);
        conversation.setAppConfig(JSON.toJSONString(new ImageReqVO()));
        conversation.setFromScene(AppSceneEnum.WEB_ADMIN.name());
        conversation.setEndUser(Long.toString(userId));
        logAppConversationMapper.insert(conversation);
        return conversation;
    }

    /**
     * 更新会话记录
     *
     * @param conversationUid 会话记录id
     * @param status          状态
     * @param request         请求参数
     */
    private void updateAppConversation(String conversationUid, LogStatusEnum status, ImageReqVO request) {
        LambdaUpdateWrapper<LogAppConversationDO> conversationWrapper = Wrappers.lambdaUpdate(LogAppConversationDO.class);
        conversationWrapper.eq(LogAppConversationDO::getUid, conversationUid);
        conversationWrapper.eq(LogAppConversationDO::getDeleted, Boolean.FALSE);
        conversationWrapper.set(LogAppConversationDO::getStatus, status.name());
        conversationWrapper.set(LogAppConversationDO::getAppConfig, JSON.toJSONString(request));
        logAppConversationMapper.update(null, conversationWrapper);
    }

    /**
     * 构建消息记录
     *
     * @param request      请求参数
     * @param conversation 会话记录
     * @param userId       用户id
     * @return 消息记录
     */
    private LogAppMessageDO buildAppMessageLog(ImageReqVO request,
                                               LogAppConversationDO conversation,
                                               Long userId) {
        LogAppMessageDO messageRequest = new LogAppMessageDO();
        messageRequest.setUid(IdUtil.fastSimpleUUID());
        messageRequest.setAppConversationUid(conversation.getUid());
        messageRequest.setAppUid(conversation.getUid());
        messageRequest.setAppMode(conversation.getAppMode());
        messageRequest.setAppConfig(JSON.toJSONString(request));
        messageRequest.setAppStep("TEXT_TO_IMAGE");
        messageRequest.setVariables(JSON.toJSONString(request.getImageRequest()));
        messageRequest.setMessage(request.getImageRequest().getPrompt());
        messageRequest.setMessageTokens(0);
        messageRequest.setMessageUnitPrice(new BigDecimal("0"));
        messageRequest.setCurrency("USD");
        messageRequest.setFromScene(conversation.getFromScene());
        messageRequest.setEndUser(Long.toString(userId));
        return messageRequest;
    }

}
