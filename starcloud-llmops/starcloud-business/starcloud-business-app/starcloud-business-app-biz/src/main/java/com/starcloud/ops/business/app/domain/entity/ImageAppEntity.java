package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.image.dto.ImageDTO;
import com.starcloud.ops.business.app.api.image.vo.request.ImageRequest;
import com.starcloud.ops.business.app.api.image.vo.response.ImageMessageRespVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.domain.entity.config.ImageConfigEntity;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.image.VSearchImageService;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.framework.common.api.util.ExceptionUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class ImageAppEntity extends BaseAppEntity<ImageReqVO, ImageMessageRespVO> {

    /**
     * 用户权益服务
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static UserBenefitsService benefitsService = SpringUtil.getBean(UserBenefitsService.class);

    /**
     * 图片生成服务
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static VSearchImageService vSearchImageService = SpringUtil.getBean(VSearchImageService.class);

    /**
     * AppRepository
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static AppRepository appRepository;

    /**
     * 获取 AppRepository
     *
     * @return {@link AppRepository}
     */
    @JsonIgnore
    @JSONField(serialize = false)
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
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _validate(ImageReqVO request) {
        getImageConfig().validate();
    }

    /**
     * 执行图片生成
     *
     * @param request 请求参数
     * @return {@link JsonData}
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected ImageMessageRespVO _execute(ImageReqVO request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("Text to Image Task");
        Long userId = WebFrameworkUtils.getLoginUserId();
        try {
            // 检测权益
            benefitsService.allowExpendBenefits(BenefitsTypeEnums.IMAGE.getCode(), userId);
            // 调用图片生成服务
            ImageMessageRespVO imageResponse = textToImage(request);
            // 扣除权益
            benefitsService.expendBenefits(BenefitsTypeEnums.IMAGE.getCode(), (long) imageResponse.getImages().size(), userId, request.getConversationUid());
            stopWatch.stop();

            // 计算消耗的 SD 的点数。需要乘以图片数量
            BigDecimal answerCredit = ImageUtils.countAnswerCredits(request.getImageRequest()).multiply(new BigDecimal(Integer.toString(imageResponse.getImages().size())));
            // 记录消息日志
            LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageRequest) -> {
                buildAppMessageLog(messageRequest, request, userId);
                messageRequest.setStatus(LogStatusEnum.SUCCESS.name());
                messageRequest.setAnswer(JSONUtil.toJsonStr(imageResponse.getImages()));
                // 消耗的 SD 的点数 X 100，有助于数据准确性
                messageRequest.setAnswerTokens(ImageUtils.countAnswerTokens(answerCredit));
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setTotalPrice(answerCredit.multiply(messageRequest.getAnswerUnitPrice()).setScale(4, RoundingMode.HALF_UP));
            });
            // 更新会话日志
            this.updateAppConversationLog(request.getConversationUid(), Boolean.TRUE);
            // 返回结果
            return imageResponse;
        } catch (ServiceException exception) {
            log.error("文字生成图片失败，错误码：{}, 错误信息：{}", exception.getCode(), exception.getMessage());
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageRequest) -> {
                buildAppMessageLog(messageRequest, request, userId);
                messageRequest.setStatus(LogStatusEnum.ERROR.name());
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setErrorCode(Integer.toString(exception.getCode()));
                messageRequest.setErrorMsg(ExceptionUtil.stackTraceToString(exception));
            });
            throw exception;
        } catch (Exception exception) {
            log.error("文字生成图片失败，错误码：{}, 错误信息：{}", Integer.toString(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode()), exception.getMessage());
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageRequest) -> {
                buildAppMessageLog(messageRequest, request, userId);
                messageRequest.setStatus(LogStatusEnum.ERROR.name());
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setErrorCode(Integer.toString(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode()));
                messageRequest.setErrorMsg(ExceptionUtil.stackTraceToString(exception));
            });

            throw ServiceExceptionUtil.exception(new ErrorCode(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode(), exception.getMessage()));
        }
    }

    /**
     * 异步执行图片生成
     *
     * @param request 请求参数
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _aexecute(ImageReqVO request) {
        this._execute(request);
    }

    /**
     * 执行后执行
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _afterExecute(ImageReqVO request, Throwable throwable) {
        SseEmitter sseEmitter = request.getSseEmitter();
        if (sseEmitter != null) {
            if (throwable != null) {
                sseEmitter.completeWithError(throwable);
            } else {
                sseEmitter.complete();
            }
        }
    }

    /**
     * 创建会话日志记录准备，属性赋值
     *
     * @param request
     * @param createRequest
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _createAppConversationLog(ImageReqVO request, LogAppConversationCreateReqVO createRequest) {
        createRequest.setAppConfig(JSONUtil.toJsonStr(request.getImageRequest()));
    }

    /**
     * 历史记录初始化
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _initHistory(ImageReqVO request, LogAppConversationDO logAppConversation, List<LogAppMessageDO> logAppMessageList) {

    }

    /**
     * 解析会话配置
     *
     * @param conversationConfig 会话配置
     * @return {@link ImageConfigEntity}
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected ImageConfigEntity _parseConversationConfig(String conversationConfig) {
        return null;
    }

    /**
     * 新增应用
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _insert() {
        getAppRepository().insert(this);
    }

    /**
     * 更新应用
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void _update() {
        getAppRepository().update(this);
    }

    /**
     * 生成图片
     *
     * @param request 请求参数
     * @return {@link ImageMessageRespVO}
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ImageMessageRespVO textToImage(ImageReqVO request) {
        ImageRequest imageRequest = request.getImageRequest();
        List<ImageDTO> imageList = vSearchImageService.textToImage(request.getImageRequest());
        ImageMessageRespVO imageResponse = new ImageMessageRespVO();
        imageResponse.setPrompt(request.getImageRequest().getPrompt());
        imageResponse.setNegativePrompt(ImageUtils.handleNegativePrompt(request.getImageRequest().getNegativePrompt(), Boolean.FALSE));
        imageResponse.setEngine(request.getImageRequest().getEngine());
        imageResponse.setWidth(request.getImageRequest().getWidth());
        imageResponse.setHeight(request.getImageRequest().getHeight());
        imageResponse.setSteps(request.getImageRequest().getSteps());
        imageResponse.setCreateTime(LocalDateTime.now());
        imageResponse.setImages(imageList);
        return imageResponse;
    }

    /**
     * 构建消息记录
     *
     * @param request      请求参数
     * @param conversation 会话记录
     * @param userId       用户id
     * @return 消息记录
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void buildAppMessageLog(LogAppMessageCreateReqVO messageRequest, ImageReqVO request, Long userId) {
        messageRequest.setAppConversationUid(request.getConversationUid());
        messageRequest.setAppUid(request.getConversationUid());
        messageRequest.setAppMode(AppModelEnum.BASE_GENERATE_IMAGE.name());
        messageRequest.setAppConfig(JSONUtil.toJsonStr(request.getImageRequest()));
        messageRequest.setAppStep("BASE_GENERATE_IMAGE");
        messageRequest.setVariables(JSONUtil.toJsonStr(request.getImageRequest()));
        messageRequest.setMessage(request.getImageRequest().getPrompt());
        messageRequest.setMessageTokens(0);
        messageRequest.setMessageUnitPrice(new BigDecimal("0.0000"));
        messageRequest.setAnswerTokens(0);
        messageRequest.setAnswerUnitPrice(new BigDecimal("0.0100"));
        messageRequest.setTotalPrice(new BigDecimal("0.0000"));
        messageRequest.setCurrency("USD");
        messageRequest.setFromScene(StringUtils.isBlank(request.getScene()) ? AppSceneEnum.WEB_IMAGE.name() : request.getScene());
        messageRequest.setEndUser(request.getEndUser());
    }
}
