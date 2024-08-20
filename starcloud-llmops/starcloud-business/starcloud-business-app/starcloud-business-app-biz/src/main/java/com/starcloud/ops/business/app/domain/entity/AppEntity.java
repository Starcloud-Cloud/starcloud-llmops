package com.starcloud.ops.business.app.domain.entity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServerException;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.system.api.permission.PermissionApi;
import cn.kstry.framework.core.bpmn.enums.BpmnTypeEnum;
import cn.kstry.framework.core.engine.StoryEngine;
import cn.kstry.framework.core.engine.facade.ReqBuilder;
import cn.kstry.framework.core.engine.facade.StoryRequest;
import cn.kstry.framework.core.engine.facade.TaskResponse;
import cn.kstry.framework.core.enums.TrackingTypeEnum;
import cn.kstry.framework.core.monitor.FieldTracking;
import cn.kstry.framework.core.monitor.MonitorTracking;
import cn.kstry.framework.core.monitor.NodeTracking;
import cn.kstry.framework.core.monitor.RecallStory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.CaseFormat;
import com.starcloud.ops.business.app.constant.WorkflowConstants;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteReqVO;
import com.starcloud.ops.business.app.controller.admin.app.vo.AppExecuteRespVO;
import com.starcloud.ops.business.app.domain.cache.AppStepStatusCache;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowConfigEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.AssembleActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.PosterActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.VariableActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.domain.manager.AppAlarmManager;
import com.starcloud.ops.business.app.domain.manager.AppDefaultConfigManager;
import com.starcloud.ops.business.app.domain.repository.app.AppRepository;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppModelEnum;
import com.starcloud.ops.business.app.enums.app.AppSceneEnum;
import com.starcloud.ops.business.app.enums.app.AppTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.exception.ActionResponseException;
import com.starcloud.ops.business.app.service.xhs.material.CreativeMaterialManager;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.verification.VerificationUtils;
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
import java.util.stream.Collectors;

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

    @JsonIgnore
    @JSONField(serialize = false)
    private static CreativeMaterialManager creativeMaterialManager = SpringUtil.getBean(CreativeMaterialManager.class);

    /**
     * 工作流引擎
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private StoryEngine storyEngine = SpringUtil.getBean(StoryEngine.class);

    /**
     * 权限 API
     */
    private PermissionApi permissionApi = SpringUtil.getBean(PermissionApi.class);

    /**
     * 应用报警管理
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private AppAlarmManager appAlarmManager = SpringUtil.getBean(AppAlarmManager.class);

    /**
     * 应用报警管理
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private AppDefaultConfigManager appDefaultConfigManager = SpringUtil.getBean(AppDefaultConfigManager.class);

    /**
     * 模版方法：基础校验
     *
     * @param request 请求参数
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected List<Verification> doValidate(AppExecuteReqVO request, ValidateTypeEnum validateType) {

        List<Verification> verifications = new ArrayList<>();

        // 应用配置校验
        WorkflowConfigEntity configuration = this.getWorkflowConfig();
        VerificationUtils.notNullApp(verifications, configuration, getUid(),
                "应用配置信息不能为空！");
        if (Objects.isNull(configuration)) {
            return verifications;
        }

        // 应用步骤列表校验
        List<WorkflowStepWrapper> stepWrappers = configuration.stepWrapperList();
        VerificationUtils.notEmptyApp(verifications, stepWrappers, getUid(),
                "应用最少需要配置一个步骤！");
        if (CollectionUtil.isEmpty(stepWrappers)) {
            return verifications;
        }

        // 如果类型为媒体素材
        if (AppTypeEnum.MEDIA_MATRIX.name().equals(this.getType())) {
            // 媒体矩阵类型应用最少需要三个步骤，分别为：上传素材，笔记生成，图片生成
            if (stepWrappers.size() < 3) {
                VerificationUtils.addVerificationApp(verifications, getUid(),
                        "媒体矩阵类型应用【" + this.getName() + "】最少需要三个步骤！分别为：【上传素材】，【笔记生成】，【图片生成】");
            }

            // 第一个步骤必须是：上传素材步骤，有且只有一个
            if (!(stepWrappers.get(0).equalsHandler(MaterialActionHandler.class) && configuration.isOnlyoneHandler(MaterialActionHandler.class))) {
                VerificationUtils.addVerificationApp(verifications, getUid(),
                        "媒体矩阵类型应用【" + this.getName() + "】第一个步骤必须是【上传素材】步骤！且有且只能有一个！");
            }

            // 倒数第二个必须包含笔记生成步骤, 有且只有一个
            if (!(stepWrappers.get(stepWrappers.size() - 2).equalsHandler(AssembleActionHandler.class) && configuration.isOnlyoneHandler(AssembleActionHandler.class))) {
                VerificationUtils.addVerificationApp(verifications, getUid(),
                        "媒体矩阵类型应用【" + this.getName() + "】倒数第二个步骤必须是【笔记生成】步骤！且有且只能有一个！");
            }

            // 最后一个步骤必须是图片生成步骤, 有且只有一个
            if (!(stepWrappers.get(stepWrappers.size() - 1).equalsHandler(PosterActionHandler.class) && configuration.isOnlyoneHandler(PosterActionHandler.class))) {
                VerificationUtils.addVerificationApp(verifications, getUid(),
                        "媒体矩阵类型应用【" + this.getName() + "】最后一个步骤必须是【图片生成】步骤！且有且只能有一个！");
            }

            // 如果存在变量步骤，变量不能为空
            List<WorkflowStepWrapper> variableStepList = stepWrappers.stream()
                    .filter(item -> item.equalsHandler(VariableActionHandler.class))
                    .collect(Collectors.toList());
            if (variableStepList.size() > 1) {
                VerificationUtils.addVerificationApp(verifications, getUid(),
                        "媒体矩阵类型应用【" + this.getName() + "】最多只能有一个【全局变量】步骤！");
            }

            // 如果存在变量步骤，最少需要配置一个变量
            for (WorkflowStepWrapper variableStep : variableStepList) {
                VariableEntity variable = variableStep.getVariable();
                if (variable == null || CollectionUtil.isEmpty(variable.getVariables())) {
                    VerificationUtils.addVerificationApp(verifications, getUid(),
                            "媒体矩阵类型应用【" + this.getName() + "】变量步骤【" + variableStep.getName() + "】最少需要配置一个变量！");
                }
            }

        }

        // 配置校验
        List<Verification> configurationValidationList = configuration.validate(getUid(), validateType);
        verifications.addAll(configurationValidationList);

        return verifications;
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
        AppContext appContext;
        try {
            log.info("应用执行【开始执行】: 应用UID: {}, 执行场景: {}, 会话UID: {}",
                    this.getUid(), request.getScene(), request.getConversationUid());

            // 权益检测
            this.allowExpendBenefits(AdminUserRightsTypeEnum.MAGIC_BEAN, request.getUserId());

            // 构建应用上下文
            appContext = new AppContext(this, AppSceneEnum.valueOf(request.getScene()));
            appContext.setUserId(request.getUserId());
            appContext.setEndUser(request.getEndUser());
            appContext.setConversationUid(request.getConversationUid());
            appContext.setSseEmitter(request.getSseEmitter());
            appContext.setMediumUid(request.getMediumUid());
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
            log.error("应用执行【执行失败】: 应用UID: {}, 执行场景: {}, 会话UID: {}, \n\t错误码: {}, 错误消息: {}",
                    this.getUid(), request.getScene(), request.getConversationUid(), exception.getCode(), exception.getMessage());
            // 创建应用消息日志
            String messageUid = this.createAppMessageLog(request, exception);
            exception.setBizUid(messageUid);
            exception.setScene(request.getScene());
            throw exception;

        } catch (Exception exception) {
            ErrorCode errorCode = ErrorCodeConstants.EXECUTE_APP_FAILURE;
            log.error("应用执行【执行失败】: 应用UID: {}, 执行场景: {}, 会话UID: {}, \n\t错误码: {}, 错误消息: {}",
                    this.getUid(), request.getScene(), request.getConversationUid(), errorCode.getCode(), exception.getMessage());
            // 创建应用消息日志
            String messageUid = this.createAppMessageLog(request, exception);
            ServiceException serviceException = ServiceExceptionUtil.exceptionWithCause(errorCode, exception);
            serviceException.setBizUid(messageUid);
            serviceException.setScene(request.getScene());
            throw serviceException;
        }

        // 执行工作流应用
        AppExecuteRespVO response = fire(appContext);

        log.info("应用执行【执行成功】: 应用UID: {}, 执行场景: {}, 会话UID: {}",
                this.getUid(), request.getScene(), request.getConversationUid());
        return response;
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
        this.updateAppConfig(request);
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
        if (throwable != null) {
            Map<String, Object> extended = request.getExtended();
            boolean isSendAlarm = true;
            if (MapUtil.isNotEmpty(extended)) {
                Object isSendAlarmObject = extended.get("isSendAlarm");
                if (Objects.nonNull(isSendAlarmObject)) {
                    if (isSendAlarmObject instanceof Boolean) {
                        isSendAlarm = (Boolean) isSendAlarmObject;
                    }
                    if (isSendAlarmObject instanceof String) {
                        isSendAlarm = Boolean.parseBoolean((String) isSendAlarmObject);
                    }
                    if (isSendAlarmObject instanceof Number) {
                        isSendAlarm = ((Number) isSendAlarmObject).intValue() == 1;
                    }
                }
            }

            if (isSendAlarm) {
                // 发送告警信息
                request.setAppName(this.getName());
                request.setMode(AppModelEnum.COMPLETION.name());
                appAlarmManager.executeAlarm(request, throwable);
            }
        }

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
     * 模版方法：获取应用的 AI 模型类型。
     * 1. 只在创建会话记录时使用
     *
     * @param request 请求参数
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    protected String getLlmModelType(AppExecuteReqVO request) {
        Map<String, String> modelMapping = appDefaultConfigManager.defaultLlmModelTypeMap();
        // 如果传入了 AI 模型类型，使用传入的
        if (StringUtils.isNotBlank(request.getAiModel())) {
            // 从配置中获取
            ModelTypeEnum modelType = appDefaultConfigManager.getLlmModelType(request.getAiModel(), request.getUserId(), Boolean.FALSE, modelMapping);
            // 返回模型类型
            return modelType.getName();
        }

        // 如果没有传入步骤 ID，使用第一步参数信息
        if (StringUtils.isBlank(request.getStepId())) {
            return null;
        }

        WorkflowStepWrapper stepWrapper = this.getWorkflowConfig().getStepWrapper(request.getStepId());

        if (stepWrapper == null) {
            return null;
        }

        VariableItemEntity modelVariable = stepWrapper.getModelVariableItem(AppConstants.MODEL);
        if (modelVariable == null) {
            return null;
        }

        if (Objects.nonNull(modelVariable.getValue())) {
            String model = String.valueOf(modelVariable.getValue());
            ModelTypeEnum modelType = appDefaultConfigManager.getLlmModelType(model, request.getUserId(), Boolean.FALSE, modelMapping);
            return modelType.getName();
        }

        // 如果没有找到模型变量
        return null;
    }

    /**
     * 更新应用配置
     *
     * @param request 请求参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void updateAppConfig(AppExecuteReqVO request) {

        Map<String, String> modelMapping = appDefaultConfigManager.defaultLlmModelTypeMap();
        // 如果传入了 AI 模型类型，使用传入的
        if (StringUtils.isNotBlank(request.getAiModel())) {
            // 从配置中获取
            ModelTypeEnum modelType = appDefaultConfigManager.getLlmModelType(request.getAiModel(), request.getUserId(), Boolean.FALSE, modelMapping);
            // 更新步骤中的模型变量
            List<WorkflowStepWrapper> stepWrappers = this.getWorkflowConfig().stepWrapperList();
            for (WorkflowStepWrapper stepWrapper : stepWrappers) {
                if (stepWrapper == null) {
                    continue;
                }
                VariableItemEntity modeVariableItem = stepWrapper.getModelVariableItem(AppConstants.MODEL);
                if (modeVariableItem == null) {
                    continue;
                }
                // 更新变量
                stepWrapper.putModelVariable(AppConstants.MODEL, modelType.getName());
            }
            // 更新配置
            this.getWorkflowConfig().setSteps(stepWrappers);
            return;
        }

        // 更新应用配置信息
        List<WorkflowStepWrapper> stepWrappers = this.getWorkflowConfig().stepWrapperList();

        for (WorkflowStepWrapper stepWrapper : stepWrappers) {
            if (stepWrapper == null) {
                continue;
            }

            VariableItemEntity modeVariableItem = stepWrapper.getModelVariableItem(AppConstants.MODEL);
            if (modeVariableItem == null) {
                continue;
            }

            // 获取模型类型
            String model = Optional.ofNullable(modeVariableItem.getValue())
                    .map(String::valueOf)
                    .orElse(null);

            // 从配置中获取
            ModelTypeEnum modelType = appDefaultConfigManager.getLlmModelType(model, request.getUserId(), Boolean.FALSE, modelMapping);
            // 更新变量值
            stepWrapper.putModelVariable(AppConstants.MODEL, modelType.getName());
        }
        // 更新配置
        this.getWorkflowConfig().setSteps(stepWrappers);
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
        // 绑定空素材库
        WorkflowStepWrapper materialStep = this.getWorkflowConfig().getStepWrapperWithoutError(MaterialActionHandler.class);
        if (Objects.nonNull(materialStep)) {
            materialStep.putVariable(CreativeConstants.LIBRARY_QUERY, "");
        }
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
        try {
            // 组装请求信息
            StoryRequest<ActionResponse> fireRequest = ReqBuilder.returnType(ActionResponse.class)
                    .trackingType(TrackingTypeEnum.SERVICE_DETAIL)
                    .timeout(WorkflowConstants.WORKFLOW_TASK_TIMEOUT)
                    .startId(appContext.getConversationUid())
                    .request(appContext)
                    .recallStoryHook(this.recallStoryHook(appContext))
                    .build();

            // 执行工作流
            TaskResponse<ActionResponse> taskResponse = storyEngine.fire(fireRequest);

            // 校验并且处理结果
            ActionResponse result = validateAndHandlerResponse(taskResponse, appContext);

            // 组装并且返回结果
            AppExecuteRespVO appExecuteResponse = new AppExecuteRespVO();
            appExecuteResponse.setSuccess(Boolean.TRUE);
            appExecuteResponse.setResultCode(taskResponse.getResultCode());
            appExecuteResponse.setResultDesc(taskResponse.getResultDesc());
            appExecuteResponse.setResult(result.getOutput().getData());
            appExecuteResponse.setConversationUid(appContext.getConversationUid());
            appExecuteResponse.setIsSendSseAll(result.getIsSendSseAll());
            return appExecuteResponse;

        } catch (ServiceException exception) {
            log.error("应用执行【执行失败】: 应用UID: {}, 执行场景: {}, 会话UID: {}, \n\t错误码: {}, 错误消息: {}",
                    this.getUid(), appContext.getScene().name(), appContext.getConversationUid(), exception.getCode(), exception.getMessage());
            exception.setScene(appContext.getScene().name());
            throw exception;

        } catch (Exception exception) {
            ErrorCode errorCode = ErrorCodeConstants.EXECUTE_APP_FAILURE;
            log.error("应用执行【执行失败】: 应用UID: {}, 执行场景: {}, 会话UID: {}, \n\t错误码: {}, 错误消息: {}",
                    this.getUid(), appContext.getScene().name(), appContext.getConversationUid(), errorCode.getCode(), exception.getMessage(), exception);

            ServiceException serviceException = ServiceExceptionUtil.exceptionWithCause(errorCode, exception);
            serviceException.setScene(appContext.getScene().name());
            throw serviceException;
        }
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
            log.info("应用执行【工作流回调开始】");
            List<NodeTracking> nodeTrackingList = Optional.ofNullable(story.getMonitorTracking())
                    .map(MonitorTracking::getStoryTracking)
                    .orElseThrow(() -> exception(ErrorCodeConstants.EXECUTE_APP_RESULT_NON_EXISTENT));

            for (NodeTracking nodeTracking : nodeTrackingList) {
                if (BpmnTypeEnum.SERVICE_TASK.equals(nodeTracking.getNodeType())) {
                    //把业务的异常传入进来
                    this.createAppMessageLog(appContext, nodeTracking, story.getException());
                }
            }
            log.info("应用执行【工作流回调结束】");
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

            //此时 appContext.getStepId(); 是最后一个执行成功的step
            String stepId = nodeTracking.getNodeName();
            ActionResponse actionResponse = this.getTracking(nodeTracking.getNoticeTracking(), ActionResponse.class);
            // 将执行结果设置到上下文中
            appContext.setActionResponse(stepId, actionResponse);
            // 获取step变量信息
            Map<String, Object> variables = appContext.getContextVariablesValues(stepId);

            // 基础信息填充
            messageCreateRequest.setAppConversationUid(appContext.getConversationUid());
            messageCreateRequest.setAppStep(stepId);
            messageCreateRequest.setEndUser(appContext.getEndUser());
            messageCreateRequest.setCreator(String.valueOf(appContext.getUserId()));
            messageCreateRequest.setUpdater(String.valueOf(appContext.getUserId()));
            messageCreateRequest.setFromScene(appContext.getScene().name());
            messageCreateRequest.setMediumUid(appContext.getMediumUid());
            messageCreateRequest.setCreateTime(nodeTracking.getStartTime());
            messageCreateRequest.setUpdateTime(nodeTracking.getStartTime());
            messageCreateRequest.setElapsed(nodeTracking.getSpendTime());
            messageCreateRequest.setCurrency("USD");
            messageCreateRequest.setAppConfig(JsonUtils.toJsonString(this));
            messageCreateRequest.setVariables(JsonUtils.toJsonString(variables));

            // actionResponse 不为空说明已经执行成功
            if (Objects.nonNull(actionResponse)) {
                messageCreateRequest.setStatus(actionResponse.getSuccess() ? LogStatusEnum.SUCCESS.name() : LogStatusEnum.ERROR.name());
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

            // 说明步骤执行异常
            messageCreateRequest.setStatus(LogStatusEnum.ERROR.name());
            messageCreateRequest.setCostPoints(0);
            messageCreateRequest.setErrorCode(String.valueOf(ErrorCodeConstants.EXECUTE_APP_FAILURE.getCode()));
            WorkflowStepWrapper stepWrapper = appContext.getStepWrapper(stepId);
            VariableItemEntity modeVariableItem = stepWrapper.getModelVariableItem(AppConstants.MODEL);

            String llmModel = Optional.ofNullable(modeVariableItem)
                    .map(VariableItemEntity::getValue)
                    .map(String::valueOf)
                    .orElse(null);

            messageCreateRequest.setAiModel(llmModel);
            // 处理异常
            if (storyException.isPresent()) {

                Throwable throwable = storyException.get();
                messageCreateRequest.setErrorMsg(ExceptionUtil.stackTraceToString(throwable));

                // ActionResponseException
                if (throwable instanceof ActionResponseException ||
                        (Objects.nonNull(throwable.getCause()) && throwable.getCause() instanceof ActionResponseException)) {
                    // 转为具体的异常
                    ActionResponseException actionResponseException = (throwable instanceof ActionResponseException) ?
                            (ActionResponseException) throwable : (ActionResponseException) throwable.getCause();

                    // 设置错误码
                    messageCreateRequest.setErrorCode(String.valueOf(actionResponseException.getCode()));

                    // 获取结果信息，填充到日志中
                    ActionResponse failureResponse = actionResponseException.getResponse();
                    if (failureResponse == null) {
                        return;
                    }
                    messageCreateRequest.setVariables(JsonUtils.toJsonString(failureResponse.getStepConfig()));
                    messageCreateRequest.setMessage(Optional.ofNullable(failureResponse.getMessage()).orElse(StringUtils.EMPTY));
                    messageCreateRequest.setMessageTokens(Optional.ofNullable(failureResponse.getMessageTokens()).map(Long::intValue).orElse(0));
                    messageCreateRequest.setMessageUnitPrice(Optional.ofNullable(failureResponse.getMessageUnitPrice()).orElse(BigDecimal.ZERO));
                    messageCreateRequest.setAnswer(Optional.ofNullable(failureResponse.getAnswer()).orElse(StringUtils.EMPTY));
                    messageCreateRequest.setAnswerTokens(Optional.ofNullable(failureResponse.getAnswerTokens()).map(Long::intValue).orElse(0));
                    messageCreateRequest.setAnswerUnitPrice(Optional.ofNullable(failureResponse.getAnswerUnitPrice()).orElse(BigDecimal.ZERO));
                    messageCreateRequest.setTotalPrice(Optional.ofNullable(failureResponse.getTotalPrice()).orElse(BigDecimal.ZERO));
                    messageCreateRequest.setCostPoints(Optional.ofNullable(failureResponse.getCostPoints()).orElse(0));
                    messageCreateRequest.setAiModel(TokenCalculator.fromName(failureResponse.getAiModel()).name());
                    return;
                }

                // ServiceException
                if (throwable instanceof ServiceException ||
                        (Objects.nonNull(throwable.getCause()) && throwable.getCause() instanceof ServiceException)) {
                    // 转为具体的异常
                    ServiceException serviceException = (throwable instanceof ServiceException) ?
                            (ServiceException) throwable : (ServiceException) throwable.getCause();
                    // 错误码填充到日志中
                    messageCreateRequest.setErrorCode(String.valueOf(serviceException.getCode()));
                }
            } else {
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

            WorkflowStepWrapper stepWrapper = appContext.getStepWrapper(appContext.getStepId());
            VariableItemEntity modeVariableItem = stepWrapper.getModelVariableItem(AppConstants.MODEL);

            String llmModel = Optional.ofNullable(modeVariableItem)
                    .map(VariableItemEntity::getValue)
                    .map(String::valueOf)
                    .orElse(null);

            BigDecimal messageUnitPrice = BigDecimal.ZERO;
            BigDecimal answerUnitPrice = BigDecimal.ZERO;
            if (StringUtils.isNotBlank(llmModel)) {
                ModelTypeEnum modelType = TokenCalculator.fromName(llmModel);
                messageUnitPrice = TokenCalculator.getUnitPrice(modelType, Boolean.TRUE);
                answerUnitPrice = TokenCalculator.getUnitPrice(modelType, Boolean.FALSE);
            }

            messageCreateRequest.setAppConversationUid(request.getConversationUid());
            messageCreateRequest.setAppStep(appContext.getStepId());
            messageCreateRequest.setFromScene(request.getScene());
            messageCreateRequest.setAiModel(llmModel);
            messageCreateRequest.setMediumUid(request.getMediumUid());
            messageCreateRequest.setAppConfig(JsonUtils.toJsonString(this));
            messageCreateRequest.setVariables(JsonUtils.toJsonString(variablesValues));
            messageCreateRequest.setStatus(LogStatusEnum.ERROR.name());
            messageCreateRequest.setMessage(String.valueOf(variablesValues.getOrDefault(AppConstants.PROMPT, "")));
            messageCreateRequest.setMessageTokens(0);
            messageCreateRequest.setMessageUnitPrice(messageUnitPrice);
            messageCreateRequest.setAnswer("");
            messageCreateRequest.setAnswerTokens(0);
            messageCreateRequest.setAnswerUnitPrice(answerUnitPrice);
            messageCreateRequest.setTotalPrice(BigDecimal.ZERO);
            messageCreateRequest.setCurrency("USD");
            messageCreateRequest.setErrorCode(String.valueOf(ErrorCodeConstants.EXECUTE_APP_FAILURE.getCode()));
            messageCreateRequest.setEndUser(request.getEndUser());
            messageCreateRequest.setCreator(String.valueOf(request.getUserId()));
            messageCreateRequest.setUpdater(String.valueOf(request.getUserId()));
            messageCreateRequest.setCreateTime(LocalDateTime.now());
            messageCreateRequest.setUpdateTime(LocalDateTime.now());
            messageCreateRequest.setElapsed(100L);
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

    /**
     * 校验工作流执行结果
     *
     * @param taskResponse 工作流执行结果
     * @return 执行结果
     */
    private ActionResponse validateAndHandlerResponse(TaskResponse<ActionResponse> taskResponse, AppContext appContext) {

        // 如果异常存在异常，则处理后抛出该异常。
        Throwable resultException = taskResponse.getResultException();
        if (Objects.nonNull(resultException)) {
            // 如果是 ActionResponseException 异常，包装成 ServiceException 异常，外不需要感知该异常
            if (resultException instanceof ActionResponseException ||
                    (Objects.nonNull(resultException.getCause()) && resultException.getCause() instanceof ActionResponseException)) {
                // 转为具体的异常
                ActionResponseException actionResponseException = (resultException instanceof ActionResponseException) ?
                        (ActionResponseException) resultException : (ActionResponseException) resultException.getCause();
                ErrorCode errorCode = new ErrorCode(actionResponseException.getCode(), actionResponseException.getMessage());
                // 转为 ServiceException 异常并且抛出
                throw exceptionWithCause(errorCode, resultException);
            }

            // 如果是 ServiceException 异常，直接抛出
            if (resultException instanceof ServiceException ||
                    (Objects.nonNull(resultException.getCause()) && resultException.getCause() instanceof ServiceException)) {

                throw (resultException instanceof ServiceException) ? (ServiceException) resultException : (ServiceException) resultException.getCause();
            }

            // 其他异常: 转为 ServiceException 异常并且抛出
            throw exceptionWithCause(ErrorCodeConstants.EXECUTE_APP_FAILURE, resultException.getMessage(), resultException);
        }

        // 如果执行失败，抛出异常
        if (!taskResponse.isSuccess()) {
            throw exception(ErrorCodeConstants.EXECUTE_APP_FAILURE, taskResponse.getResultDesc());
        }

        /*
         * 因为执行到最后 stepId 就是最后的节点
         */
        ActionResponse response = appContext.getStepResponse(appContext.getStepId());

        // 如果执行结果不存在，抛出异常
        if (Objects.isNull(response)) {
            throw exceptionWithMessage(ErrorCodeConstants.EXECUTE_APP_FAILURE, "应用执行失败，执行结果不存在！");
        }

        // 如果执行结果失败，抛出异常
        if (Objects.isNull(response.getSuccess()) || !response.getSuccess()) {
            int errorCode = response.transformErrorCode();
            String errorMessage = response.transformErrorMessage(appContext.getStepId());
            throw exception(new ErrorCode(errorCode, errorMessage));
        }

        // 如果执行结果为空，抛出异常
        String answer = response.getAnswer();
        if (StringUtils.isBlank(answer)) {
            throw exceptionWithMessage(ErrorCodeConstants.EXECUTE_APP_FAILURE, "应用执行失败，执行结果为空！");
        }

        // 如果执行结果为空，抛出异常
        if (Objects.isNull(response.getOutput()) || Objects.isNull(response.getOutput().getData())) {
            throw exceptionWithMessage(ErrorCodeConstants.EXECUTE_APP_FAILURE, "应用执行失败，执行结果为空！");
        }

        response.setAnswer(answer.trim());
        return response;
    }

}
