package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.JSON;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.response.ImageMessageRespVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.image.VSearchImageService;
import com.starcloud.ops.business.app.util.ImageMetaUtil;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-20
 */
@SuppressWarnings("all")
@Slf4j
@Data
public class ImageAppEntity extends BaseAppEntity<ImageReqVO, JsonData> {

    /**
     * 用户权益服务
     */
    private static UserBenefitsService benefitsService = SpringUtil.getBean(UserBenefitsService.class);

    /**
     * 图片生成服务
     */
    private static VSearchImageService vSearchImageService = SpringUtil.getBean(VSearchImageService.class);

    /**
     * AppRepository
     */
    private static AppRepository appRepository;

    /**
     * 获取 AppRepository
     *
     * @return {@link AppRepository}
     */
    public static AppRepository getAppRepository() {
        if (appRepository == null) {
            appRepository = SpringUtil.getBean(AppRepository.class);
        }
        return appRepository;
    }

    /**
     * 基础数据校验
     */
    @Override
    protected void _validate() {
        getImageConfig().validate();
    }

    /**
     * 历史记录初始化
     */
    @Override
    protected void _initHistory(ImageReqVO imageRequest, LogAppConversationDO logAppConversation, List<LogAppMessageDO> logAppMessageList) {
        //preHistory(request.getConversationUid(), AppModelEnum.CHAT.name());
        ImageConfigEntity imageConfigEntity = this._parseConversationConfig(logAppConversation.getAppConfig());

        // 用 req 的参数 在去覆盖默认参数
    }

    /**
     * 创建会话日志记录准备，属性赋值
     *
     * @param imageRequest
     * @param logAppConversationRequest
     */
    @Override
    protected void _createAppConversationLog(ImageReqVO imageRequest, LogAppConversationCreateReqVO logAppConversationRequest) {
        logAppConversationRequest.setAppMode(AppModelEnum.BASE_GENERATE_IMAGE.name());
        logAppConversationRequest.setAppUid(imageRequest.getAppUid());
        logAppConversationRequest.setAppName(this.getName());
        logAppConversationRequest.setStatus(LogStatusEnum.ERROR.name());
        logAppConversationRequest.setAppConfig(JSON.toJSONString(this.getImageConfig()));
        logAppConversationRequest.setFromScene(AppSceneEnum.WEB_ADMIN.name());
        logAppConversationRequest.setEndUser(imageRequest.getEndUser());
    }

    /**
     * 执行图片生成
     *
     * @param request 请求参数
     * @return {@link JsonData}
     */
    @Override
    protected JsonData _execute(ImageReqVO request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Text to Image Task");
        Long userId = WebFrameworkUtils.getLoginUserId();
        try {
            // 检测权益
            benefitsService.allowExpendBenefits(BenefitsTypeEnums.IMAGE.getCode(), userId);
            // 调用图片生成服务
            List<ImageDTO> imageList = vSearchImageService.textToImage(request.getImageRequest());
            // 扣除权益
            benefitsService.expendBenefits(BenefitsTypeEnums.IMAGE.getCode(), (long) imageList.size(), userId, request.getConversationUid());
            stopWatch.stop();
            LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageRequest) -> {
                buildAppMessageLog(messageRequest, request, userId);
                messageRequest.setStatus(LogStatusEnum.SUCCESS.name());
                messageRequest.setAnswer(JSON.toJSONString(imageList));
                // todo 价格统计
                messageRequest.setAnswerTokens(imageList.size());
                messageRequest.setMessageUnitPrice(new BigDecimal("0"));
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setTotalPrice(new BigDecimal(Integer.toString(imageList.size())));
            });
            // 更新会话日志
            this.updateAppConversationLog(request.getConversationUid(), Boolean.TRUE);
            // 处理返回结果
            ImageMessageRespVO imageResponse = new ImageMessageRespVO();
            imageResponse.setPrompt(request.getImageRequest().getPrompt());
            imageResponse.setCreateTime(LocalDateTime.now());
            imageResponse.setImages(imageList);
            JsonData jsonData = new JsonData();
            jsonData.setData(imageResponse);
            jsonData.setJsonSchemas(JSON.toJSONString(request));
            return jsonData;
        } catch (ServiceException exception) {
            stopWatch.stop();
            LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageRequest) -> {
                buildAppMessageLog(messageRequest, request, userId);
                messageRequest.setStatus(LogStatusEnum.ERROR.name());
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setErrorCode(Integer.toString(exception.getCode()));
                messageRequest.setErrorMsg(exception.getMessage());
            });
            log.error("文字生成图片失败，错误码：{}, 错误信息：{}", exception.getCode(), exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            stopWatch.stop();
            LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageRequest) -> {
                buildAppMessageLog(messageRequest, request, userId);
                messageRequest.setStatus(LogStatusEnum.ERROR.name());
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setErrorCode(Integer.toString(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode()));
                messageRequest.setErrorMsg(exception.getMessage());
            });
            log.error("文字生成图片失败，错误码：{}, 错误信息：{}", appMessage.getErrorCode(), exception.getMessage());
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), exception.getMessage());
        }
    }

    /**
     * 异步执行图片生成
     *
     * @param request 请求参数
     */
    @Override
    protected void _aexecute(ImageReqVO request) {
        this._execute(request);
        request.getSseEmitter().complete();
    }

    /**
     * 解析会话配置
     *
     * @param conversationConfig 会话配置
     * @return {@link ImageConfigEntity}
     */
    @Override
    protected ImageConfigEntity _parseConversationConfig(String conversationConfig) {
        ImageConfigEntity imageConfig = JSON.parseObject(conversationConfig, ImageConfigEntity.class);
        return imageConfig;
    }

    /**
     * 新增应用
     */
    @Override
    protected void _insert() {
        getAppRepository().insert(this);
    }

    /**
     * 更新应用
     */
    @Override
    protected void _update() {
        getAppRepository().update(this);
    }

    /**
     * 构建消息记录
     *
     * @param request      请求参数
     * @param conversation 会话记录
     * @param userId       用户id
     * @return 消息记录
     */
    private void buildAppMessageLog(LogAppMessageCreateReqVO messageRequest, ImageReqVO request, Long userId) {
        messageRequest.setAppConversationUid(request.getConversationUid());
        messageRequest.setAppUid(request.getAppUid());
        messageRequest.setAppMode(AppModelEnum.BASE_GENERATE_IMAGE.name());
        messageRequest.setAppConfig(JSON.toJSONString(this.getImageConfig()));
        messageRequest.setAppStep("BASE_GENERATE_IMAGE");
        messageRequest.setVariables(JSON.toJSONString(request.getImageRequest()));
        messageRequest.setMessage(request.getImageRequest().getPrompt());
        messageRequest.setMessageTokens(ImageMetaUtil.countTokens(request.getImageRequest().getPrompt()));
        messageRequest.setMessageUnitPrice(new BigDecimal("0"));
        messageRequest.setCurrency("USD");
        messageRequest.setFromScene(AppSceneEnum.WEB_ADMIN.name());
        messageRequest.setEndUser(Long.toString(userId));
    }


}
