package com.starcloud.ops.business.app.service.image.impl;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageReqVO;
import com.starcloud.ops.business.app.api.image.vo.request.TextToImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.ImageRespVO;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.image.ImageService;
import com.starcloud.ops.business.app.service.image.VSearchImageService;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.LogAppApi;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

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
    private LogAppApi logAppApi;

    @Resource
    private VSearchImageService vSearchImageService;

    /**
     * 查询历史图片列表
     *
     * @return 图片列表
     */
    @Override
    public ImageRespVO historyGenerateImages() {
        return null;
    }

    /**
     * 文字生成图片
     *
     * @param request 请求参数
     * @return 图片列表
     */
    @Override
    public List<ImageDTO> textToImage(ImageReqVO request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Text to Image Task");
        Long userId = WebFrameworkUtils.getLoginUserId();
        // 会话记录
        LogAppConversationCreateReqVO conversation = this.createConversation(request, userId);
        try {
            // 检测权益
            benefitsService.allowExpendBenefits(BenefitsTypeEnums.IMAGE.getCode(), userId);
            // 调用图片生成服务
            List<ImageDTO> imageList = vSearchImageService.textToImage((TextToImageRequest) request.getImageRequest());
            // 扣除权益
            benefitsService.expendBenefits(BenefitsTypeEnums.IMAGE.getCode(), (long) imageList.size(), userId, conversation.getUid());
            // 更新会话记录
            logAppApi.updateAppConversationStatus(conversation.getUid(), LogStatusEnum.SUCCESS);
            // 消息记录
            stopWatch.stop();
            LogAppMessageCreateReqVO messageRequest = buildAppMessageLog(request, conversation, userId);
            messageRequest.setStatus(LogStatusEnum.SUCCESS.name());
            messageRequest.setAnswer(JSON.toJSONString(imageList));
            messageRequest.setAnswerTokens(imageList.size());
            messageRequest.setMessageUnitPrice(new BigDecimal("0"));
            messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
            messageRequest.setTotalPrice(new BigDecimal(Integer.toString(imageList.size())));
            logAppApi.createAppMessage(messageRequest);

            return imageList;
        } catch (ServiceException e) {
            stopWatch.stop();
            // 消息记录
            LogAppMessageCreateReqVO messageRequest = buildAppMessageLog(request, conversation, userId);
            messageRequest.setStatus(LogStatusEnum.ERROR.name());
            messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
            messageRequest.setErrorCode(Integer.toString(e.getCode()));
            messageRequest.setErrorMsg(e.getMessage());
            logAppApi.createAppMessage(messageRequest);
            // 更新会话记录
            logAppApi.updateAppConversationStatus(conversation.getUid(), LogStatusEnum.ERROR);
            log.error("文字生成图片失败，错误码：{}, 错误信息：{}", e.getCode(), e.getMessage(), e);
            return null;
        } catch (Exception e) {
            stopWatch.stop();
            // 消息记录
            LogAppMessageCreateReqVO messageRequest = buildAppMessageLog(request, conversation, userId);
            messageRequest.setStatus(LogStatusEnum.ERROR.name());
            messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
            messageRequest.setErrorCode("300300000");
            messageRequest.setErrorMsg(e.getMessage());
            logAppApi.createAppMessage(messageRequest);
            // 更新会话记录
            logAppApi.updateAppConversationStatus(conversation.getUid(), LogStatusEnum.ERROR);
            log.error("文字生成图片失败，错误码：{}, 错误信息：{}", messageRequest.getErrorCode(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 创建会话记录，有则查询返回，无则创建
     *
     * @param request 请求参数
     * @param userId  用户id
     * @return 会话记录
     */
    private LogAppConversationCreateReqVO createConversation(ImageReqVO request, Long userId) {
        LogAppConversationCreateReqVO conversation = new LogAppConversationCreateReqVO();
        conversation.setUid(request.getConversationId());
        conversation.setAppMode(AppModelEnum.IMAGE.name());
        conversation.setAppName(request.getName());
        conversation.setStatus(LogStatusEnum.ERROR.name());
        conversation.setAppUid(request.getConversationId());
        conversation.setAppConfig(JSON.toJSONString(request));
        conversation.setFromScene(AppSceneEnum.WEB_ADMIN.name());
        conversation.setEndUser(Long.toString(userId));
        return logAppApi.createAppConversation(conversation);
    }

    /**
     * 构建消息记录
     *
     * @param request      请求参数
     * @param conversation 会话记录
     * @param userId       用户id
     * @return 消息记录
     */
    private LogAppMessageCreateReqVO buildAppMessageLog(ImageReqVO request,
                                                        LogAppConversationCreateReqVO conversation,
                                                        Long userId) {
        LogAppMessageCreateReqVO messageRequest = new LogAppMessageCreateReqVO();
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

        messageRequest.setCurrency(null);
        messageRequest.setFromScene(conversation.getFromScene());
        messageRequest.setEndUser(Long.toString(userId));

        return messageRequest;
    }
}
