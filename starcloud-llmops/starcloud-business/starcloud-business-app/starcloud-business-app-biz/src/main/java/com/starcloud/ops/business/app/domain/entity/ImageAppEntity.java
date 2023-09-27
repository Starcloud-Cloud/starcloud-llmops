package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.api.image.vo.response.BaseImageResponse;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageReqVO;
import com.starcloud.ops.business.app.controller.admin.image.vo.ImageRespVO;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.service.vsearch.VSearchService;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
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
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-20
 */
@SuppressWarnings("all")
@Slf4j
@Data
public class ImageAppEntity extends BaseAppEntity<ImageReqVO, ImageRespVO> {

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
    private static VSearchService vSearchService = SpringUtil.getBean(VSearchService.class);

    /**
     * AppRepository
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static AppRepository appRepository = SpringUtil.getBean(AppRepository.class);


    /**
     * 基础数据校验
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void doValidate(ImageReqVO request) {
        getImageConfig().validate();
    }

    /**
     * 获取当前执行记录的主体用户，会做主体用户做如下操作。默认是当前用户态
     * 1，扣除权益
     * 2，记录日志
     *
     * @param req
     * @return 用户 ID
     */
    @Override
    protected Long getRunUserId(ImageReqVO req) {
        return SecurityFrameworkUtils.getLoginUserId();
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
    protected ImageRespVO doExecute(ImageReqVO request) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(request.getAppUid() + " Task");
        Long userId = request.getUserId();
        try {
            // 检测权益
            this.allowExpendBenefits(BenefitsTypeEnums.IMAGE.getCode(), userId);
            // 调用图片生成服务
            BaseImageResponse imageResponse = request.getImageHandler().handle(request.getImageRequest());
            imageResponse.setFromScene(request.getScene());
            // 扣除权益
            benefitsService.expendBenefits(BenefitsTypeEnums.IMAGE.getCode(), (long) imageResponse.getImages().size(), userId, request.getConversationUid());
            stopWatch.stop();

            // 记录消息日志
            LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageRequest) -> {
                buildAppMessageLog(messageRequest, request, userId);
                messageRequest.setStatus(LogStatusEnum.SUCCESS.name());
                messageRequest.setAnswer(JSONUtil.toJsonStr(imageResponse));
                // 直接用图片数量作为扣费数量
                messageRequest.setAnswerTokens(imageResponse.getImages().size());
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setTotalPrice(new BigDecimal(String.valueOf(imageResponse.getImages().size())));
                request.getImageHandler().handleLogMessage(messageRequest, request.getImageRequest(), imageResponse);
            });
            // 返回结果
            ImageRespVO imageRespVO = new ImageRespVO();
            imageRespVO.setConversationUid(request.getConversationUid());
            imageRespVO.setResponse(imageResponse);
            return imageRespVO;
        } catch (ServiceException exception) {
            log.error("处理图片失败，错误码：{}, 错误信息：{}", exception.getCode(), exception.getMessage());
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageRequest) -> {
                buildAppMessageLog(messageRequest, request, userId);
                messageRequest.setStatus(LogStatusEnum.ERROR.name());
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setErrorCode(Integer.toString(exception.getCode()));
                messageRequest.setErrorMsg(ExceptionUtil.stackTraceToString(exception));
                request.getImageHandler().handleLogMessage(messageRequest, request.getImageRequest(), null);
            });
            throw exception;
        } catch (Exception exception) {
            log.error("处理图片失败，错误码：{}, 错误信息：{}", Integer.toString(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode()), exception.getMessage());
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageRequest) -> {
                buildAppMessageLog(messageRequest, request, userId);
                messageRequest.setStatus(LogStatusEnum.ERROR.name());
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setErrorCode(Integer.toString(ErrorCodeConstants.GENERATE_IMAGE_FAIL.getCode()));
                messageRequest.setErrorMsg(ExceptionUtil.stackTraceToString(exception));
                request.getImageHandler().handleLogMessage(messageRequest, request.getImageRequest(), null);
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
    protected void doAsyncExecute(ImageReqVO request) {
        this.doExecute(request);
    }

    /**
     * 模版方法：执行应用前置处理方法
     *
     * @param imageReqVO 请求参数
     */
    @Override
    protected void beforeExecute(ImageReqVO imageReqVO) {

    }

    /**
     * 执行后执行
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void afterExecute(ImageReqVO request, Throwable throwable) {
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
    protected void buildAppConversationLog(ImageReqVO request, LogAppConversationCreateReqVO createRequest) {
        createRequest.setAppConfig(JSONUtil.toJsonStr(request.getImageRequest()));
    }

    /**
     * 历史记录初始化
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void initHistory(ImageReqVO request, LogAppConversationDO logAppConversation, List<LogAppMessageDO> logAppMessageList) {

    }

    /**
     * 新增应用
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void doInsert() {
        appRepository.insert(this);
    }

    /**
     * 更新应用
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void doUpdate() {
        appRepository.update(this);
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
        messageRequest.setAppUid(this.getUid());
        messageRequest.setAppMode(StringUtils.isBlank(request.getMode()) ? AppModelEnum.IMAGE.name() : request.getMode());
        messageRequest.setAppConfig(JSONUtil.toJsonStr(this));
        messageRequest.setAppStep(request.getScene());
        messageRequest.setVariables(JSONUtil.toJsonStr(request.getImageRequest()));
        messageRequest.setMessage("");
        messageRequest.setMessageTokens(0);
        messageRequest.setMessageUnitPrice(new BigDecimal("0.0000"));
        messageRequest.setAnswerTokens(0);
        messageRequest.setAnswerUnitPrice(new BigDecimal("0.0000"));
        messageRequest.setTotalPrice(new BigDecimal("0.0000"));
        messageRequest.setCurrency("USD");
        messageRequest.setFromScene(request.getScene());
        messageRequest.setMediumUid(request.getMediumUid());
        messageRequest.setEndUser(request.getEndUser());
    }
}
