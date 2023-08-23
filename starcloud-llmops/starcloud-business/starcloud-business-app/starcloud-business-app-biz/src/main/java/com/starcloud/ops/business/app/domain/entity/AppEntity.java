package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServerException;
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
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppConversationDO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;
import com.starcloud.ops.framework.common.api.util.ExceptionUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
                throw ServiceExceptionUtil.exception(new ErrorCode(ErrorCodeConstants.APP_MARKET_FAIL.getCode(), "步骤名称不能重复"));
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
        return Long.valueOf(this.getCreator());
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
            this.allowExpendBenefits(BenefitsTypeEnums.TOKEN.getCode(), request.getUserId());
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
            log.info("应用工作流执行: 应用参数：{}\n", JSONUtil.parse(appContext.getContextVariablesValues()).toStringPretty());
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
        StoryRequest<Void> fireRequest = ReqBuilder.returnType(Void.class).build();
        fireRequest.setTrackingType(TrackingTypeEnum.SERVICE_DETAIL);
        fireRequest.setTimeout(WorkflowConstants.WORKFLOW_TASK_TIMEOUT);
        fireRequest.setStartId(appContext.getConversationUid());
        fireRequest.setRequest(appContext);
        fireRequest.setRecallStoryHook(this.recallStoryHook(appContext));

        // 执行工作流
        TaskResponse<Void> fire = storyEngine.fire(fireRequest);

        // 如果异常，抛出异常
        if (fire.getResultException() != null) {
            log.info("应用工作流执行异常: 步骤 ID: {}", appContext.getStepId());
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_FAIL, fire.getResultException());
        }

        // 如果执行失败，抛出异常
        if (!fire.isSuccess()) {
            log.info("应用工作流执行异常: 步骤 ID: {}", appContext.getStepId());
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_FAIL, fire.getResultDesc());
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
        log.info("应用执行回调开始...");
        return (story) -> {
            List<NodeTracking> nodeTrackingList = Optional.ofNullable(story.getMonitorTracking()).map(MonitorTracking::getStoryTracking)
                    .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_EXECUTE_FAIL, "unknown result"));

            log.info("应用执行回调基本信息: {} {} {} ", story.getBusinessId(), story.getStartId(), story.getResult().isPresent());
            for (NodeTracking nodeTracking : nodeTrackingList) {
                if (BpmnTypeEnum.SERVICE_TASK.equals(nodeTracking.getNodeType())) {
                    log.info("应用执行回调: 记录日志消息开始");
                    this.createAppMessageLog(appContext, nodeTracking);
                    log.info("应用执行回调: 记录日志消息结束");
                }
            }
            log.info("应用执行回调结束");
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
        this.createAppMessage((messageCreateReqVO) -> {
            messageCreateReqVO.setCreator(String.valueOf(appContext.getUserId()));
            messageCreateReqVO.setEndUser(appContext.getEndUser());
            messageCreateReqVO.setAppConversationUid(appContext.getConversationUid());
            messageCreateReqVO.setAppStep(appContext.getStepId());
            messageCreateReqVO.setCreateTime(nodeTracking.getStartTime());
            messageCreateReqVO.setUpdateTime(nodeTracking.getStartTime());
            messageCreateReqVO.setElapsed(nodeTracking.getSpendTime());
            messageCreateReqVO.setFromScene(appContext.getScene().name());
            messageCreateReqVO.setMediumUid(appContext.getMediumUid());
            messageCreateReqVO.setCurrency("USD");

            ActionResponse actionResponse = this.getTracking(nodeTracking.getNoticeTracking(), ActionResponse.class);
            // todo 避免因为异常获取不到元素的值，从 appContext 中获取原始的值
            if (actionResponse != null) {
                appContext.setActionResponse(actionResponse);
                AppRespVO appRespVO = AppConvert.INSTANCE.convertResponse(appContext.getApp());
                messageCreateReqVO.setAppConfig(JSONUtil.toJsonStr(appRespVO));
                messageCreateReqVO.setStatus(actionResponse.getSuccess() ? LogStatusEnum.SUCCESS.name() : LogStatusEnum.ERROR.name());
                messageCreateReqVO.setVariables(JSONUtil.toJsonStr(actionResponse.getStepConfig()));
                messageCreateReqVO.setMessage(actionResponse.getMessage());
                messageCreateReqVO.setMessageTokens(actionResponse.getMessageTokens().intValue());
                messageCreateReqVO.setMessageUnitPrice(actionResponse.getMessageUnitPrice());
                messageCreateReqVO.setAnswer(actionResponse.getAnswer());
                messageCreateReqVO.setAnswerTokens(actionResponse.getAnswerTokens().intValue());
                messageCreateReqVO.setAnswerUnitPrice(actionResponse.getAnswerUnitPrice());
                messageCreateReqVO.setTotalPrice(actionResponse.getTotalPrice());
                messageCreateReqVO.setErrorCode(actionResponse.getErrorCode());
                messageCreateReqVO.setErrorMsg(actionResponse.getErrorMsg());
            }
            // 如果异常，设置异常信息
            if (nodeTracking.getTaskException() != null) {
                messageCreateReqVO.setStatus(LogStatusEnum.ERROR.name());
                messageCreateReqVO.setErrorMsg(ExceptionUtil.stackTraceToString(nodeTracking.getTaskException()));
                messageCreateReqVO.setErrorCode("010");
                if (nodeTracking.getTaskException() instanceof KstryException) {
                    messageCreateReqVO.setErrorCode(((KstryException) nodeTracking.getTaskException()).getErrorCode());
                }
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
        this.createAppMessage((messageCreateReqVO) -> {
            messageCreateReqVO.setCreator(String.valueOf(request.getUserId()));
            messageCreateReqVO.setEndUser(request.getEndUser());
            messageCreateReqVO.setAppConversationUid(request.getConversationUid());
            messageCreateReqVO.setAppStep(request.getStepId());
            messageCreateReqVO.setElapsed(100L);
            messageCreateReqVO.setFromScene(request.getScene());
            messageCreateReqVO.setMediumUid(request.getMediumUid());
            messageCreateReqVO.setCurrency("USD");
            messageCreateReqVO.setAppConfig(JSONUtil.toJsonStr(this));
            messageCreateReqVO.setStatus(LogStatusEnum.ERROR.name());
            messageCreateReqVO.setMessage("");
            messageCreateReqVO.setMessageTokens(0);
            messageCreateReqVO.setMessageUnitPrice(new BigDecimal("0.02"));
            messageCreateReqVO.setAnswer("");
            messageCreateReqVO.setAnswerTokens(0);
            messageCreateReqVO.setAnswerUnitPrice(new BigDecimal("0.02"));
            messageCreateReqVO.setTotalPrice(new BigDecimal("0"));
            if (exception instanceof ServerException) {
                messageCreateReqVO.setErrorCode(String.valueOf(((ServerException) exception).getCode()));
            }
            messageCreateReqVO.setErrorMsg(ExceptionUtil.stackTraceToString(exception));
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
