package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
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
import com.starcloud.ops.business.app.service.image.strategy.handler.BaseImageHandler;
import com.starcloud.ops.business.app.service.vsearch.VSearchService;
import com.starcloud.ops.business.app.util.ImageUtils;
import com.starcloud.ops.business.app.util.UserRightSceneUtils;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.framework.common.api.util.ExceptionUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-20
 */
@SuppressWarnings("all")
@Slf4j
@Data
public class ImageAppEntity extends BaseAppEntity<ImageReqVO, ImageRespVO> {

    @JsonIgnore
    @JSONField(serialize = false)
    private static AdminUserRightsApi adminUserRightsApi = SpringUtil.getBean(AdminUserRightsApi.class);

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
        // 图片处理器
        BaseImageHandler imageHandler = request.getImageHandler();
        if (Objects.isNull(imageHandler)) {
            LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageRequest) -> {
                buildAppMessageLog(messageRequest, request);
                messageRequest.setStatus(LogStatusEnum.ERROR.name());
                if (stopWatch.isRunning()) {
                    stopWatch.stop();
                }
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setErrorCode(String.valueOf(ErrorCodeConstants.EXECUTE_IMAGE_HANDLER_NOT_FOUND.getCode()));
                messageRequest.setErrorMsg(ErrorCodeConstants.EXECUTE_IMAGE_HANDLER_NOT_FOUND.getMsg());
            });
            throw exception(ErrorCodeConstants.EXECUTE_IMAGE_HANDLER_NOT_FOUND);
        }
        try {
            // 检测权益
            this.allowExpendBenefits(AdminUserRightsTypeEnum.MAGIC_IMAGE, request.getUserId());

            // 调用图片生成服务
            BaseImageResponse imageResponse = imageHandler.handle(request.getImageRequest());
            if (Objects.isNull(imageResponse) || CollectionUtil.isEmpty(imageResponse.getImages())) {
                throw exception(ErrorCodeConstants.GENERATE_IMAGE_EMPTY);
            }
            imageResponse.setFromScene(request.getScene());
            imageResponse.setFinishTime(new Date());
            // 扣除权益
            Integer costPoints = imageHandler.getCostPoints(request.getImageRequest(), imageResponse);
            adminUserRightsApi.reduceRights(
                    request.getUserId(), // 用户ID
                    AdminUserRightsTypeEnum.MAGIC_IMAGE, // 权益类型
                    costPoints, // 权益点数
                    UserRightSceneUtils.getUserRightsBizType(request.getScene()).getType(), // 业务类型
                    request.getConversationUid() // 会话ID
            );
            stopWatch.stop();

            // 记录消息日志
            LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageRequest) -> {
                buildAppMessageLog(messageRequest, request);
                messageRequest.setStatus(LogStatusEnum.SUCCESS.name());
                messageRequest.setAnswer(JSONUtil.toJsonStr(imageResponse));
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setCostPoints(costPoints);
                imageHandler.handleLogMessage(messageRequest, request.getImageRequest(), imageResponse);
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
                buildAppMessageLog(messageRequest, request);
                messageRequest.setStatus(LogStatusEnum.ERROR.name());
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setErrorCode(Integer.toString(exception.getCode()));
                messageRequest.setErrorMsg(ExceptionUtil.stackTraceToString(exception));
                imageHandler.handleLogMessage(messageRequest, request.getImageRequest(), null);
            });
            throw exception;
        } catch (Exception exception) {
            log.error("处理图片失败，错误码：{}, 错误信息：{}", Integer.toString(ErrorCodeConstants.EXECUTE_IMAGE_FAILURE.getCode()), exception.getMessage());
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
            LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageRequest) -> {
                buildAppMessageLog(messageRequest, request);
                messageRequest.setStatus(LogStatusEnum.ERROR.name());
                messageRequest.setElapsed(stopWatch.getTotalTimeMillis());
                messageRequest.setErrorCode(Integer.toString(ErrorCodeConstants.EXECUTE_IMAGE_FAILURE.getCode()));
                messageRequest.setErrorMsg(ExceptionUtil.stackTraceToString(exception));
                imageHandler.handleLogMessage(messageRequest, request.getImageRequest(), null);
            });

            throw exception(new ErrorCode(ErrorCodeConstants.EXECUTE_IMAGE_FAILURE.getCode(), exception.getMessage()));
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
     * 模版方法：获取应用的 AI 模型类型
     *
     * @param request 请求参数
     */
    @Override
    protected String obtainLlmAiModelType(ImageReqVO request) {
        return request.getImageHandler().obtainEngine(request.getImageRequest());
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
     * @return 消息记录
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void buildAppMessageLog(LogAppMessageCreateReqVO messageRequest, ImageReqVO request) {
        messageRequest.setAppConversationUid(request.getConversationUid());
        messageRequest.setAppUid(this.getUid());
        messageRequest.setAppMode(StringUtils.isBlank(request.getMode()) ? AppModelEnum.IMAGE.name() : request.getMode());
        messageRequest.setAppStep(request.getScene());
        messageRequest.setFromScene(request.getScene());
        messageRequest.setAiModel(this.obtainLlmAiModelType(request));
        messageRequest.setAppConfig(JSONUtil.toJsonStr(this));
        messageRequest.setVariables(JSONUtil.toJsonStr(request.getImageRequest()));
        messageRequest.setMessage("");
        messageRequest.setMessageTokens(0);
        messageRequest.setMessageUnitPrice(new BigDecimal("0.0000"));
        messageRequest.setAnswerTokens(0);
        messageRequest.setAnswerUnitPrice(ImageUtils.SD_PRICE);
        messageRequest.setTotalPrice(new BigDecimal("0.0000"));
        messageRequest.setCurrency("USD");
        messageRequest.setCostPoints(0);
        messageRequest.setElapsed(0L);
        messageRequest.setStatus(LogStatusEnum.ERROR.name());
        messageRequest.setMediumUid(request.getMediumUid());
        messageRequest.setEndUser(request.getEndUser());
        messageRequest.setCreator(String.valueOf(request.getUserId()));
        messageRequest.setUpdater(String.valueOf(request.getUserId()));
        messageRequest.setCreateTime(LocalDateTime.now());
        messageRequest.setUpdateTime(LocalDateTime.now());
    }
}
