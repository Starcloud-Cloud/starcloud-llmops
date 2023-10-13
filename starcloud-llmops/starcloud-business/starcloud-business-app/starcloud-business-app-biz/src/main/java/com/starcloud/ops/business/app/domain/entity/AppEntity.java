package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServerException;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.kstry.framework.core.bpmn.enums.BpmnTypeEnum;
import cn.kstry.framework.core.engine.StoryEngine;
import cn.kstry.framework.core.engine.facade.ReqBuilder;
import cn.kstry.framework.core.engine.facade.StoryRequest;
import cn.kstry.framework.core.engine.facade.TaskResponse;
import cn.kstry.framework.core.enums.TrackingTypeEnum;
import cn.kstry.framework.core.exception.KstryException;
import cn.kstry.framework.core.monitor.MonitorTracking;
import cn.kstry.framework.core.monitor.NodeTracking;
import cn.kstry.framework.core.monitor.NoticeTracking;
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
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.limits.enums.BenefitsTypeEnums;
import com.starcloud.ops.business.limits.service.userbenefits.UserBenefitsService;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.framework.common.api.util.ExceptionUtil;
import com.starcloud.ops.llm.langchain.core.schema.ModelTypeEnum;
import com.starcloud.ops.llm.langchain.core.utils.TokenCalculator;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.math.BigDecimal;
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
     * 用户权益服务
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static UserBenefitsService userBenefitsService = SpringUtil.getBean(UserBenefitsService.class);

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
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_STEP_NAME_DUPLICATE.getCode(), stepWrapper.getName());
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
            this.allowExpendBenefits(BenefitsTypeEnums.COMPUTATIONAL_POWER.getCode(), request.getUserId());
            // 构建应用上下文
            appContext = new AppContext(this, AppSceneEnum.valueOf(request.getScene()));
            appContext.setUserId(request.getUserId());
            appContext.setEndUser(request.getEndUser());
            appContext.setConversationUid(request.getConversationUid());
            appContext.setSseEmitter(request.getSseEmitter());
            appContext.setMediumUid(request.getMediumUid());
            if (StringUtils.isNotBlank(request.getStepId())) {
                appContext.setStepId(request.getStepId());
            } else {
                request.setStepId(appContext.getStepId());
            }
            log.info("应用工作流执行: 应用参数：\n{}", JSONUtil.parse(appContext.getContextVariablesValues()).toStringPretty());
        } catch (ServerException exception) {
            log.info("应用工作流执行异常(ServerException): 错误信息: {}", exception.getMessage());
            this.createAppMessageLog(request, exception);
            throw exception;
        } catch (Exception exception) {
            log.info("应用工作流执行异常(exception): 错误信息: {}", exception.getMessage());
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
    protected void doAsyncExecute(AppExecuteReqVO request) {
        this.doExecute(request);
    }

    /**
     * 模版方法：执行应用前置处理方法
     *
     * @param appExecuteReqVO 请求参数
     */
    @Override
    protected void beforeExecute(AppExecuteReqVO appExecuteReqVO) {

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
    protected void afterExecute(AppExecuteReqVO request, Throwable throwable) {
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
     * 模版方法：创建会话记录
     *
     * @param request       请求参数
     * @param createRequest 请求参数
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected void buildAppConversationLog(AppExecuteReqVO request, LogAppConversationCreateReqVO createRequest) {
        createRequest.setAppConfig(JSONUtil.toJsonStr(this.getWorkflowConfig()));
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
        StoryRequest<Void> fireRequest = ReqBuilder.returnType(Void.class)
                .trackingType(TrackingTypeEnum.SERVICE_DETAIL)
                .timeout(WorkflowConstants.WORKFLOW_TASK_TIMEOUT)
                .startId(appContext.getConversationUid())
                .request(appContext)
                .recallStoryHook(this.recallStoryHook(appContext))
                .build();

        // 执行工作流
        TaskResponse<Void> fire = storyEngine.fire(fireRequest);

        // 如果异常，抛出异常
        if (Objects.nonNull(fire.getResultException())) {
            Throwable resultException = fire.getResultException();
            log.info("应用工作流执行异常: 步骤 ID: {}, 错误信息: {}", appContext.getStepId(), resultException.getMessage());
            if (resultException instanceof ServiceException) {
                throw (ServiceException) resultException;
            }
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_FAILURE.getCode(), resultException.getMessage());
        }

        // 如果执行失败，抛出异常
        if (!fire.isSuccess()) {
            log.info("应用工作流执行异常: 步骤 ID: {}", appContext.getStepId());
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_FAILURE, fire.getResultDesc());
        }

        log.info("应用工作流执行成功: 步骤 ID: {}", appContext.getStepId());
        return AppExecuteRespVO.success(fire.getResultCode(), fire.getResultDesc(), fire.getResult());

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
                    this.createAppMessageLog(appContext, nodeTracking);
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
    private void createAppMessageLog(AppContext appContext, NodeTracking nodeTracking) {
        this.createAppMessage((messageCreateRequest) -> {

            messageCreateRequest.setCreator(String.valueOf(appContext.getUserId()));
            messageCreateRequest.setEndUser(appContext.getEndUser());
            messageCreateRequest.setAppConversationUid(appContext.getConversationUid());
            messageCreateRequest.setAppStep(appContext.getStepId());
            messageCreateRequest.setCreateTime(nodeTracking.getStartTime());
            messageCreateRequest.setUpdateTime(nodeTracking.getStartTime());
            messageCreateRequest.setElapsed(nodeTracking.getSpendTime());
            messageCreateRequest.setFromScene(appContext.getScene().name());
            messageCreateRequest.setMediumUid(appContext.getMediumUid());
            messageCreateRequest.setCurrency("USD");

            // 获取所有变量
            Map<String, Object> variables = appContext.getContextVariablesValues();
            String aiModel = String.valueOf(Optional.ofNullable(variables.get("MODEL")).orElse(ModelTypeEnum.GPT_3_5_TURBO.getName()));
            messageCreateRequest.setAiModel(aiModel);

            // actionResponse 不为空说明已经执行成功
            ActionResponse actionResponse = this.getTracking(nodeTracking.getNoticeTracking(), ActionResponse.class);
            if (Objects.nonNull(actionResponse)) {
                // 将执行结果数据更新到 app
                appContext.setActionResponse(actionResponse);
                AppRespVO appRespVO = AppConvert.INSTANCE.convertResponse(appContext.getApp());
                messageCreateRequest.setStatus(LogStatusEnum.SUCCESS.name());
                messageCreateRequest.setAppConfig(JSONUtil.toJsonStr(appRespVO));
                messageCreateRequest.setVariables(JSONUtil.toJsonStr(actionResponse.getStepConfig()));
                messageCreateRequest.setMessage(actionResponse.getMessage());
                messageCreateRequest.setMessageTokens(actionResponse.getMessageTokens().intValue());
                messageCreateRequest.setMessageUnitPrice(actionResponse.getMessageUnitPrice());
                messageCreateRequest.setAnswer(actionResponse.getAnswer());
                messageCreateRequest.setAnswerTokens(actionResponse.getAnswerTokens().intValue());
                messageCreateRequest.setAnswerUnitPrice(actionResponse.getAnswerUnitPrice());
                messageCreateRequest.setTotalPrice(actionResponse.getTotalPrice());
                return;
            }

            ModelTypeEnum modelType = TokenCalculator.fromName(aiModel);
            BigDecimal messageUnitPrice = TokenCalculator.getUnitPrice(modelType, Boolean.TRUE);
            BigDecimal answerUnitPrice = TokenCalculator.getUnitPrice(modelType, Boolean.FALSE);

            // 说明执行失败
            messageCreateRequest.setStatus(LogStatusEnum.ERROR.name());
            messageCreateRequest.setAppConfig(JSONUtil.toJsonStr(this));
            messageCreateRequest.setVariables(JSONUtil.toJsonStr(variables));
            messageCreateRequest.setMessage(String.valueOf(Optional.ofNullable(variables.get("PROMPT")).orElse("")));
            messageCreateRequest.setMessageTokens(0);
            messageCreateRequest.setMessageUnitPrice(messageUnitPrice);
            messageCreateRequest.setAnswer("");
            messageCreateRequest.setAnswerTokens(0);
            messageCreateRequest.setAnswerUnitPrice(answerUnitPrice);
            messageCreateRequest.setTotalPrice(new BigDecimal("0"));
            Optional<Throwable> taskExceptionOptional = Optional.ofNullable(nodeTracking.getTaskException());
            if (taskExceptionOptional.isPresent()) {
                Throwable throwable = taskExceptionOptional.get();
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
    private void createAppMessageLog(AppExecuteReqVO request, Exception exception) {
        this.createAppMessage((messageCreateRequest) -> {
            // 构建应用上下文
            AppContext appContext = new AppContext(this, AppSceneEnum.valueOf(request.getScene()));
            Map<String, Object> variablesValues = appContext.getContextVariablesValues();
            String aiModel = String.valueOf(Optional.ofNullable(variablesValues.get("MODEL")).orElse(ModelTypeEnum.GPT_3_5_TURBO.getName()));
            ModelTypeEnum modelType = TokenCalculator.fromName(aiModel);
            BigDecimal messageUnitPrice = TokenCalculator.getUnitPrice(modelType, Boolean.TRUE);
            BigDecimal answerUnitPrice = TokenCalculator.getUnitPrice(modelType, Boolean.FALSE);

            messageCreateRequest.setCreator(String.valueOf(request.getUserId()));
            messageCreateRequest.setEndUser(request.getEndUser());
            messageCreateRequest.setAppConversationUid(request.getConversationUid());
            messageCreateRequest.setAppStep(request.getStepId());
            messageCreateRequest.setElapsed(100L);
            messageCreateRequest.setFromScene(request.getScene());
            messageCreateRequest.setAiModel(aiModel);
            messageCreateRequest.setMediumUid(request.getMediumUid());
            messageCreateRequest.setCurrency("USD");
            messageCreateRequest.setAppConfig(JSONUtil.toJsonStr(this));
            messageCreateRequest.setStatus(LogStatusEnum.ERROR.name());
            messageCreateRequest.setMessage("");
            messageCreateRequest.setMessageTokens(0);
            messageCreateRequest.setMessageUnitPrice(messageUnitPrice);
            messageCreateRequest.setAnswer("");
            messageCreateRequest.setAnswerTokens(0);
            messageCreateRequest.setAnswerUnitPrice(answerUnitPrice);
            messageCreateRequest.setTotalPrice(new BigDecimal("0"));
            messageCreateRequest.setErrorCode(String.valueOf(ErrorCodeConstants.EXECUTE_APP_FAILURE.getCode()));
            if (exception instanceof ServerException) {
                messageCreateRequest.setErrorCode(String.valueOf(((ServerException) exception).getCode()));
            }
            messageCreateRequest.setErrorMsg(ExceptionUtil.stackTraceToString(exception));

        });
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
    private <T> T getTracking(List<NoticeTracking> noticeTrackingList, Class<T> clazz) {
        String clsName = clazz.getSimpleName();
        String field = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, clsName);
        return Optional.ofNullable(noticeTrackingList).orElse(new ArrayList<>()).stream()
                .filter(noticeTracking -> noticeTracking.getFieldName().equals(field))
                .map(noticeTracking -> JSON.parseObject(noticeTracking.getValue(), clazz))
                .findFirst().orElse(null);
    }

}
