package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.ServerException;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.kstry.framework.core.bpmn.enums.BpmnTypeEnum;
import cn.kstry.framework.core.engine.StoryEngine;
import cn.kstry.framework.core.engine.facade.ReqBuilder;
import cn.kstry.framework.core.engine.facade.StoryRequest;
import cn.kstry.framework.core.engine.facade.TaskResponse;
import cn.kstry.framework.core.enums.TrackingTypeEnum;
import cn.kstry.framework.core.exception.KstryException;
import cn.kstry.framework.core.monitor.FieldTracking;
import cn.kstry.framework.core.monitor.MonitorTracking;
import cn.kstry.framework.core.monitor.NodeTracking;
import cn.kstry.framework.core.monitor.RecallStory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.CaseFormat;
import com.starcloud.ops.business.app.api.app.vo.response.AppRespVO;
import com.starcloud.ops.business.app.constant.WorkflowConstants;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.convert.app.AppConvert;
import com.starcloud.ops.business.app.domain.cache.AppStepStatusCache;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.service.chat.callback.MySseCallBackHandler;
import com.starcloud.ops.business.app.util.SseResultUtil;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import com.starcloud.ops.framework.common.api.util.ExceptionUtil;
import com.starcloud.ops.framework.common.api.util.SseEmitterUtil;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * App 实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Slf4j
@Data
public class AppEntity extends BaseAppEntity<AppExecuteReqVO, AppExecuteRespVO> {

    /**
     * 应用 AppRepository 服务
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static AppRepository appRepository = SpringUtil.getBean(AppRepository.class);

    /**
     * 步骤状态缓存
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static AppStepStatusCache appStepStatusCache = SpringUtil.getBean(AppStepStatusCache.class);

    /**
     * 工作流引擎
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private StoryEngine storyEngine = SpringUtil.getBean(StoryEngine.class);

    /**
     * 模版方法：基础校验
     *
     * @param request 请求参数
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void doValidate(AppExecuteReqVO request) {
        WorkflowConfigEntity config = this.getWorkflowConfig();
        if (config == null) {
            return;
        }
        List<WorkflowStepWrapper> stepWrappers = config.getSteps();
        for (WorkflowStepWrapper stepWrapper : stepWrappers) {
            // name 不能重复
            if (stepWrappers.stream().filter(step -> step.getName().equals(stepWrapper.getName())).count() > 1) {
                throw exception(ErrorCodeConstants.APP_STEP_NAME_DUPLICATE, stepWrapper.getName());
            }
            stepWrapper.validate();
        }
        config.setSteps(stepWrappers);
        this.setWorkflowConfig(config);
    }

    /**
     * 获取当前执行记录的主体用户，会做主体用户做如下操作。默认是当前用户态
     * 1，扣除权益
     * 2，记录日志
     *
     * @return 用户 ID
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected Long getRunUserId(AppExecuteReqVO req) {
        if (!AppSceneEnum.inLoginUserIdScene(AppSceneEnum.valueOf(req.getScene()))) {
            return Long.valueOf(this.getCreator());
        }

        return super.getRunUserId(req);
    }

    /**
     * 同步执行应用
     *
     * @param request 请求参数
     * @return 执行结果
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected AppExecuteRespVO doExecute(AppExecuteReqVO request) {
        log.info("应用工作流执行开始...");
        AppContext appContext;
        try {
            // 权益检测
            this.allowExpendBenefits(AdminUserRightsTypeEnum.MAGIC_BEAN, request.getUserId());
            // 构建应用上下文
            appContext = new AppContext(this, AppSceneEnum.valueOf(request.getScene()));
            appContext.setUserId(request.getUserId());
            appContext.setEndUser(request.getEndUser());
            appContext.setConversationUid(request.getConversationUid());
            appContext.setSseEmitter(request.getSseEmitter());
            appContext.setMediumUid(request.getMediumUid());
            appContext.setAiModel(this.obtainLlmAiModelType(request));
            appContext.setN(request.getN());
            appContext.setContinuous(request.getContinuous());

            if (StringUtils.isNotBlank(request.getStepId())) {
                appContext.setStepId(request.getStepId());
            } else {
                request.setStepId(appContext.getStepId());
                //不传入节点，默认从头开始执行到最后节点
                appContext.setContinuous(true);
            }
        } catch (ServiceException exception) {
            log.error("应用工作流执行异常(ServerException): 错误信息: {}", exception.getMessage());
            String messageUid = this.createAppMessageLog(request, exception);
            // ServiceException 时候将消息UID传入exception中
            exception.setScene(request.getScene());
            exception.setBizUid(messageUid);
            throw exception;
        } catch (Exception exception) {
            log.error("应用工作流执行异常(exception): 错误信息: {}", exception.getMessage());
            this.createAppMessageLog(request, exception);
            throw exception;
        }

        // 执行工作流应用
        return this.fire(appContext);
    }

    /**
     * 异步执行
     * log 交由具体类去实现
     *
     * @param request 请求参数
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected AppExecuteRespVO doAsyncExecute(AppExecuteReqVO request) {
        return this.doExecute(request);
    }

    /**
     * 模版方法：执行应用前置处理方法
     *
     * @param request 请求参数
     */
    @Override
    protected void beforeExecute(AppExecuteReqVO request) {
        appStepStatusCache.init(request.getConversationUid(), this);
    }

    /**
     * 模版方法：执行应用后置处理方法
     *
     * @param request   请求参数
     * @param throwable 异常
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void afterExecute(AppExecuteRespVO result, AppExecuteReqVO request, Throwable throwable) {
        SseEmitter sseEmitter = request.getSseEmitter();
        if (sseEmitter != null) {
            if (throwable != null) {
                sseEmitter.completeWithError(throwable);
            } else {

                try {

                    if (result.getIsSendSseAll()) {
                        SseEmitterUtil.send(sseEmitter, Integer.valueOf(result.getResultCode()), String.valueOf(result.getResult()));
                    }

                } catch (Exception e) {
                    log.error("AppEntity afterExecute is fail: {}", e.getMessage(), e);
                }

                sseEmitter.complete();
            }
        }
    }

    /**
     * 模版方法：创建会话记录
     *
     * @param request       请求参数
     * @param createRequest 请求参数
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void buildAppConversationLog(AppExecuteReqVO request, LogAppConversationCreateReqVO createRequest) {
        createRequest.setAppConfig(JsonUtils.toJsonString(this.getWorkflowConfig()));
    }

    /**
     * 模版方法：获取应用的 AI 模型类型
     *
     * @param request 请求参数
     */
    @Override
    protected String obtainLlmAiModelType(AppExecuteReqVO request) {
        // 如果传入了 AI 模型类型，使用传入的
        if (StringUtils.isNotBlank(request.getAiModel())) {
            return request.getAiModel();
        }

        // 如果没有传入步骤 ID，使用第一步参数信息
        if (StringUtils.isBlank(request.getStepId())) {
            return null;
        }

        WorkflowStepWrapper stepWrapper = this.getWorkflowConfig().getStepWrapper(request.getStepId());

        if (stepWrapper == null) {
            return null;
        }

        VariableItemEntity modelVariable = stepWrapper.getModeVariableItem("MODEL");
        if (modelVariable == null) {
            return null;
        }

        if (Objects.nonNull(modelVariable.getValue())) {
            return String.valueOf(modelVariable.getValue());
        }

        if (modelVariable.getDefaultValue() != null) {
            return String.valueOf(modelVariable.getDefaultValue());
        }

        // 如果没有找到模型变量
        return null;
    }

    /**
     * 模版方法：历史记录初始化
     *
     * @param request            请求参数
     * @param logAppConversation 会话记录
     * @param logAppMessageList  消息记录
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void initHistory(AppExecuteReqVO request, LogAppConversationDO logAppConversation, List<LogAppMessageDO> logAppMessageList) {

    }

    /**
     * 模版方法：新增应用
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void doInsert() {
        appRepository.insert(this);
    }

    /**
     * 模版方法：更新应用
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void doUpdate() {
        appRepository.update(this);
    }

    /**
     * 执行应用
     *
     * @param appContext 执行应用上下文
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected AppExecuteRespVO fire(@Valid AppContext appContext) {
        // 组装请信息
        StoryRequest<ActionResponse> fireRequest = ReqBuilder.returnType(ActionResponse.class)
                .trackingType(TrackingTypeEnum.SERVICE_DETAIL)
                .timeout(WorkflowConstants.WORKFLOW_TASK_TIMEOUT)
                .startId(appContext.getConversationUid())
                .request(appContext)
                .recallStoryHook(this.recallStoryHook(appContext))
                .build();

        // 执行工作流
        TaskResponse<ActionResponse> fire = storyEngine.fire(fireRequest);

        // 如果异常，抛出异常
        if (Objects.nonNull(fire.getResultException())) {
            Throwable resultException = fire.getResultException();
            log.error("应用工作流执行异常: 步骤 ID: {}, 错误信息: {}", appContext.getStepId(), resultException.getMessage());
            if (resultException instanceof ServiceException) {
                throw (ServiceException) resultException;
            }
            // 工作流框架可能会包装 ServiceException，所以尝试从 cause 中获取
            if (Objects.nonNull(resultException.getCause()) && resultException.getCause() instanceof ServiceException) {
                throw (ServiceException) resultException.getCause();
            }
            throw exception(ErrorCodeConstants.EXECUTE_APP_FAILURE, resultException.getMessage());
        }

        // 如果执行失败，抛出异常
        if (!fire.isSuccess()) {
            log.error("应用工作流执行异常: 步骤 ID: {}", appContext.getStepId());
            throw exception(ErrorCodeConstants.EXECUTE_APP_FAILURE, fire.getResultDesc());
        }

        /**
         * 因为执行到最后 stepId 就是最后的节点
         */
        ActionResponse result = appContext.getStepResponse(appContext.getStepId());

        if (Objects.isNull(result)) {
            log.error("应用工作流执行异常(ActionResponse 结果为空): 步骤 ID: {}", appContext.getStepId());
            throw exception(ErrorCodeConstants.EXECUTE_APP_RESULT_NON_EXISTENT);
        }

        if (!result.getSuccess()) {
            log.error("应用工作流执行异常(ActionResponse success 为 false): 步骤 ID: {}", appContext.getStepId());
            throw exception(ErrorCodeConstants.EXECUTE_APP_FAILURE, StringUtils.isBlank(result.getErrorMsg()) ? "执行失败" : result.getErrorMsg());
        }

        String answer = result.getAnswer();
        if (StringUtils.isBlank(answer)) {
            log.error("应用工作流执行异常(ActionResponse answer 为空): 步骤 ID: {}", appContext.getStepId());
            throw exception(ErrorCodeConstants.EXECUTE_APP_ANSWER_NOT_EXIST, appContext.getStepId());
        }
        result.setAnswer(answer.trim());

        log.info("应用工作流执行成功: 步骤 ID: {}", appContext.getStepId());

        AppExecuteRespVO appExecuteRespVO = AppExecuteRespVO.success(fire.getResultCode(), fire.getResultDesc(), result.getAnswer(), appContext.getConversationUid());
        appExecuteRespVO.setIsSendSseAll(result.getIsSendSseAll());

        //只返回内容
        return appExecuteRespVO;

    }

    /**
     * 回调方法
     *
     * @param appContext 应用上下文
     * @return 回调方法
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Consumer<RecallStory> recallStoryHook(AppContext appContext) {
        return (story) -> {
            log.info("应用执行回调开始...");
            List<NodeTracking> nodeTrackingList = Optional.ofNullable(story.getMonitorTracking()).map(MonitorTracking::getStoryTracking).orElseThrow(() -> exception(ErrorCodeConstants.EXECUTE_APP_RESULT_NON_EXISTENT));
            for (NodeTracking nodeTracking : nodeTrackingList) {
                if (BpmnTypeEnum.SERVICE_TASK.equals(nodeTracking.getNodeType())) {
                    //把业务的异常传入进来
                    this.createAppMessageLog(appContext, nodeTracking, story.getException());
                }
            }
            log.info("应用执行回调结束...");
        };
    }

    /**
     * 创建应用消息日志
     *
     * @param appContext   应用上下文
     * @param nodeTracking 节点跟踪
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private void createAppMessageLog(AppContext appContext, NodeTracking nodeTracking, Optional<Throwable> storyException) {

        this.createAppMessage((messageCreateRequest) -> {

            ActionResponse actionResponse = this.getTracking(nodeTracking.getNoticeTracking(), ActionResponse.class);
            appContext.setActionResponse(nodeTracking.getNodeName(), actionResponse);

            //此时 appContext.getStepId(); 是最后一个执行成功的step
            String stepId = nodeTracking.getNodeName();

            messageCreateRequest.setAppConversationUid(appContext.getConversationUid());
            messageCreateRequest.setAppStep(stepId);
            messageCreateRequest.setEndUser(appContext.getEndUser());
            messageCreateRequest.setCreator(String.valueOf(appContext.getUserId()));
            messageCreateRequest.setUpdater(String.valueOf(appContext.getUserId()));
            messageCreateRequest.setCreateTime(nodeTracking.getStartTime());
            messageCreateRequest.setUpdateTime(nodeTracking.getStartTime());
            messageCreateRequest.setElapsed(nodeTracking.getSpendTime());
            messageCreateRequest.setFromScene(appContext.getScene().name());
            messageCreateRequest.setMediumUid(appContext.getMediumUid());
            messageCreateRequest.setCurrency("USD");
            messageCreateRequest.setAiModel(appContext.getAiModel());

            AppRespVO appRespVO = AppConvert.INSTANCE.convertResponse(appContext.getApp());
            // 获取step变量
            Map<String, Object> variables = appContext.getContextVariablesValues(nodeTracking.getNodeName());
            messageCreateRequest.setVariables(JsonUtils.toJsonString(variables));

            // actionResponse 不为空说明已经执行成功
            if (Objects.nonNull(actionResponse)) {
                messageCreateRequest.setStatus(actionResponse.getSuccess() ? LogStatusEnum.SUCCESS.name() : LogStatusEnum.ERROR.name());
                messageCreateRequest.setAppConfig(JsonUtils.toJsonString(appRespVO));
                messageCreateRequest.setVariables(JsonUtils.toJsonString(actionResponse.getStepConfig()));
                messageCreateRequest.setMessage(actionResponse.getMessage());
                messageCreateRequest.setMessageTokens(actionResponse.getMessageTokens().intValue());
                messageCreateRequest.setMessageUnitPrice(actionResponse.getMessageUnitPrice());
                messageCreateRequest.setAnswer(actionResponse.getAnswer());
                messageCreateRequest.setAnswerTokens(actionResponse.getAnswerTokens().intValue());
                messageCreateRequest.setAnswerUnitPrice(actionResponse.getAnswerUnitPrice());
                messageCreateRequest.setTotalPrice(actionResponse.getTotalPrice());
                messageCreateRequest.setCostPoints(actionResponse.getCostPoints());
                messageCreateRequest.setAiModel(actionResponse.getAiModel());
                return;
            }

            // 说明执行handler时异常
            messageCreateRequest.setStatus(LogStatusEnum.ERROR.name());
            messageCreateRequest.setAppConfig(JsonUtils.toJsonString(appRespVO));
            messageCreateRequest.setVariables(JsonUtils.toJsonString(variables));
            messageCreateRequest.setCostPoints(0);

            if (storyException.isPresent()) {
                Throwable throwable = storyException.get();
                messageCreateRequest.setErrorMsg(ExceptionUtil.stackTraceToString(throwable));
                messageCreateRequest.setErrorCode(String.valueOf(ErrorCodeConstants.EXECUTE_APP_FAILURE.getCode()));
                if (throwable instanceof KstryException) {
                    messageCreateRequest.setErrorCode(((KstryException) throwable).getErrorCode());
                }
                if (throwable instanceof ServiceException) {
                    messageCreateRequest.setErrorCode(String.valueOf(((ServiceException) throwable).getCode()));
                }
            } else {
                messageCreateRequest.setErrorCode(String.valueOf(ErrorCodeConstants.EXECUTE_APP_FAILURE.getCode()));
                messageCreateRequest.setErrorMsg(ErrorCodeConstants.EXECUTE_APP_FAILURE.getMsg());
            }
        });
    }

    /**
     * 创建应用消息日志
     *
     * @param request   应用上下文
     * @param exception 节点跟踪
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private String createAppMessageLog(AppExecuteReqVO request, Exception exception) {
        LogAppMessageCreateReqVO appMessage = this.createAppMessage((messageCreateRequest) -> {
            // 构建应用上下文
            AppContext appContext = new AppContext(this, AppSceneEnum.valueOf(request.getScene()));
            if (StringUtils.isNotBlank(request.getStepId())) {
                appContext.setStepId(request.getStepId());
            } else {
                request.setStepId(appContext.getStepId());
            }
            Map<String, Object> variablesValues = appContext.getContextVariablesValues();
            String aiModel = this.obtainLlmAiModelType(request);
            ModelTypeEnum modelType = Objects.isNull(aiModel) ? null : TokenCalculator.fromName(aiModel);
            BigDecimal messageUnitPrice = Objects.isNull(aiModel) ? new BigDecimal("0.0") : TokenCalculator.getUnitPrice(modelType, Boolean.TRUE);
            BigDecimal answerUnitPrice = Objects.isNull(aiModel) ? new BigDecimal("0.0") : TokenCalculator.getUnitPrice(modelType, Boolean.FALSE);

            messageCreateRequest.setAppConversationUid(request.getConversationUid());
            messageCreateRequest.setAppStep(appContext.getStepId());
            messageCreateRequest.setElapsed(100L);
            messageCreateRequest.setFromScene(request.getScene());
            messageCreateRequest.setAiModel(aiModel);
            messageCreateRequest.setMediumUid(request.getMediumUid());
            messageCreateRequest.setCurrency("USD");
            messageCreateRequest.setAppConfig(JsonUtils.toJsonString(this));
            messageCreateRequest.setVariables(JsonUtils.toJsonString(variablesValues));
            messageCreateRequest.setStatus(LogStatusEnum.ERROR.name());
            messageCreateRequest.setMessage(String.valueOf(variablesValues.getOrDefault("PROMPT", "")));
            messageCreateRequest.setMessageTokens(0);
            messageCreateRequest.setMessageUnitPrice(messageUnitPrice);
            messageCreateRequest.setAnswer("");
            messageCreateRequest.setAnswerTokens(0);
            messageCreateRequest.setAnswerUnitPrice(answerUnitPrice);
            messageCreateRequest.setTotalPrice(new BigDecimal("0"));
            messageCreateRequest.setErrorCode(String.valueOf(ErrorCodeConstants.EXECUTE_APP_FAILURE.getCode()));
            messageCreateRequest.setCreator(String.valueOf(request.getUserId()));
            messageCreateRequest.setUpdater(String.valueOf(request.getUserId()));
            messageCreateRequest.setCreateTime(LocalDateTime.now());
            messageCreateRequest.setUpdateTime(LocalDateTime.now());
            messageCreateRequest.setEndUser(request.getEndUser());
            messageCreateRequest.setCostPoints(0);
            if (exception instanceof ServerException) {
                messageCreateRequest.setErrorCode(String.valueOf(((ServerException) exception).getCode()));
            }
            messageCreateRequest.setErrorMsg(ExceptionUtil.stackTraceToString(exception));

        });
        return Optional.ofNullable(appMessage).map(LogAppMessageCreateReqVO::getUid).orElse("");
    }

    /**
     * 获取追踪
     *
     * @param noticeTrackingList 追踪列表
     * @param clazz              类
     * @param <T>                类型
     * @return 追踪
     */
    @SuppressWarnings("all")
    @JsonIgnore
    @JSONField(serialize = false)
    private <T> T getTracking(List<FieldTracking> noticeTrackingList, Class<T> clazz) {
        String clsName = clazz.getSimpleName();
        String field = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clsName);
        return Optional.ofNullable(noticeTrackingList).orElse(new ArrayList<>()).stream()
                .filter(noticeTracking -> noticeTracking.getSourceName().equals(field))
                .map(noticeTracking -> JSON.parseObject(noticeTracking.getValue(), clazz))
                .findFirst().orElse(null);
    }

}
