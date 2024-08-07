package com.starcloud.ops.business.app.domain.entity.workflow.action.base;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.domain.cache.AppStepStatusCache;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.JsonDataDefSchema;
import com.starcloud.ops.business.app.domain.entity.workflow.WorkflowStepEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.AppConstants;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.exception.ActionResponseException;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import com.starcloud.ops.business.app.util.UserRightSceneUtils;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.workflow.app.process.AppProcessParser;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.api.rights.dto.ReduceRightsDTO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@Slf4j
@SuppressWarnings("all")
public abstract class BaseActionHandler extends Object {

    /**
     * 扣除权益
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static AdminUserRightsApi ADMIN_USER_RIGHTS_API = SpringUtil.getBean(AdminUserRightsApi.class);

    /**
     * 步骤状态缓存
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private static AppStepStatusCache appStepStatusCache = SpringUtil.getBean(AppStepStatusCache.class);

    /**
     * 生成个handler 实例
     *
     * @param name handler 名称
     * @return handler
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public static BaseActionHandler of(String name) {
        try {
            //头部小写驼峰
            return SpringUtil.getBean(StrUtil.lowerFirst(name));
        } catch (Exception exception) {
            log.error("获取步骤节点处理器失败：{}", name);
        }
        return null;
    }

    /**
     * 校验步骤
     *
     * @param wrapper      步骤包装器
     * @param validateType 校验类型
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public abstract List<Verification> validate(WorkflowStepWrapper wrapper, ValidateTypeEnum validateType);

    /**
     * 获取用户权益类型
     *
     * @return 权益类型
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract AdminUserRightsTypeEnum getUserRightsType();

    /**
     * 执行具体的步骤
     *
     * @param context
     * @return 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected abstract ActionResponse doExecute(AppContext context);

    /**
     * 具体步骤执行器的入参定义的{@code JsonSchema}
     *
     * @param stepWrapper 当前步骤包装器
     * @return 具体步骤执行器的入参定义的 {@code JsonSchema}
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public JsonSchema getInVariableJsonSchema(WorkflowStepWrapper stepWrapper) {
        //默认所有节点的入参都不返回支持
        return null;
    }

    /**
     * 具体步骤执行器的出参定义的{@code JsonSchema}
     *
     * @param stepWrapper 当前步骤包装器
     * @return 具体步骤执行器的出参定义的 {@code JsonSchema}
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public JsonSchema getOutVariableJsonSchema(WorkflowStepWrapper stepWrapper) {
        //如果配置了返回结构定义就获取，不然就创建一个默认的
        if (hasResponseJsonSchema(stepWrapper)) {
            String json = Optional.of(stepWrapper.getFlowStep()).map(WorkflowStepEntity::getResponse).map(ActionResponse::getOutput).map(JsonData::getJsonSchema).orElse("");
            //有配置，直接返回
            JsonSchema jsonSchema = JsonSchemaUtils.str2JsonSchema(json);
            return jsonSchema;
        } else {
            //定义一个默认的JsonSchema结构， xxx.data
            return JsonSchemaUtils.generateJsonSchema(JsonDataDefSchema.class);
        }
    }

    /**
     * 具体步骤执行器的出参定义的{@code JsonSchema}
     *
     * @param context 当前应用上下文
     * @return 具体步骤执行器的出参定义的 {@code JsonSchema}
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public JsonSchema getOutVariableJsonSchema(AppContext context) {
        WorkflowStepWrapper workflowStepWrapper = context.getStepWrapper(context.getStepId());
        return this.getOutVariableJsonSchema(workflowStepWrapper);
    }

    /**
     * 判断师傅配置了返回结果为{@code JsonSchema}
     *
     * @param stepWrapper 当前步骤包装器
     * @return 是否配置了返回结果为 {@code JsonSchema}
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Boolean hasResponseJsonSchema(WorkflowStepWrapper stepWrapper) {
        //如果配置了返回结构定义就获取，不然就创建一个默认的
        ActionResponse actionResponse = stepWrapper.getActionResponse();
        String jsonSchema = actionResponse.getJsonSchema();
        String type = actionResponse.getType();
        return AppStepResponseTypeEnum.JSON.name().equalsIgnoreCase(type) && StrUtil.isNotBlank(jsonSchema);
    }

    /**
     * 判断师傅配置了返回结果为{@code JsonSchema}
     *
     * @param workflowStepWrapper 当前步骤包装器
     * @return 是否配置了返回结果为 {@code JsonSchema}
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Boolean hasResponseJsonSchema(AppContext context) {
        WorkflowStepWrapper workflowStepWrapper = context.getStepWrapper(context.getStepId());
        return this.hasResponseJsonSchema(workflowStepWrapper);
    }

    /**
     * 流程执行器，action 执行入口。
     * 异常自己吃掉，放入到上下文中，由上层处理
     *
     * @param context           上下文
     * @param scopeDataOperator 作用域数据操作器
     * @return 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public ActionResponse execute(@ReqTaskParam(reqSelf = true) AppContext context, ScopeDataOperator scopeDataOperator) {
        AppValidate.notNull(context, "应用步骤执行失败！无法获取应用执行上下文信息，请联系管理员或稍后重试！");
        AppValidate.notNull(scopeDataOperator, "应用步骤执行失败！无法获取应用执行元数据，请联系管理员或稍后重试！");
        String stepId = this.getStepId(scopeDataOperator);
        String conversationUid = context.getConversationUid();
        AppEntity app = context.getApp();
        try {

            // 更新缓存为开始
            appStepStatusCache.stepStart(conversationUid, stepId, app);

            // 执行前的准备工作
            context.setStepId(stepId);

            // 执行具体的步骤, 传入上下文，避免多线层的影响
            ActionResponse response = this.doExecute(context);

            // 将执行结果设置到上下文中
            context.setActionResponse(response);

            // 执行结果校验, 如果失败，抛出异常
            validateResponse(response, stepId);

            // 权益扣除
            reduceRights(context, response);

            // 更新缓存为成功
            appStepStatusCache.stepSuccess(conversationUid, stepId, app);

            return response;
        } catch (ActionResponseException exception) {
            appStepStatusCache.stepFailure(conversationUid, stepId, app);
            loggerError(context, exception, "步骤");
            exception.setMessage("【" + stepId + "】步骤执行失败: " + exception.getMessage());
            throw exception;
        } catch (ServiceException exception) {
            appStepStatusCache.stepFailure(conversationUid, stepId, app);
            loggerError(context, exception, "步骤");
            exception.setMessage("【" + stepId + "】步骤执行失败: " + exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            ErrorCode errorCode = ErrorCodeConstants.EXECUTE_APP_ACTION_FAILURE;
            appStepStatusCache.stepFailure(conversationUid, stepId, app);
            loggerError(context, exception, "步骤");
            throw ServiceExceptionUtil.exceptionWithCause(errorCode, "【" + stepId + "】步骤执行失败: " + exception.getMessage(), exception);
        }
    }

    /**
     * 获取步骤ID
     *
     * @return 获取步骤ID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected String getStepId(ScopeDataOperator scopeDataOperator) {
        try {
            // 从工作流上下文中获取步骤ID
            Optional<String> property = scopeDataOperator.getTaskProperty();
            AppProcessParser.ServiceTaskPropertyDTO serviceTaskProperty = JSONUtil.toBean(property.get(), AppProcessParser.ServiceTaskPropertyDTO.class);
            return serviceTaskProperty.getStepId();
        } catch (Exception exception) {
            log.error("步骤执行失败: 获取步骤ID异常: {}", exception.getMessage());
            throw ServiceExceptionUtil.exceptionWithCause(ErrorCodeConstants.EXECUTE_APP_ACTION_FAILURE,
                    "步骤执行失败: 获取步骤ID异常！请联系管理员或稍后重试！", exception);
        }
    }

    /**
     * 获取应用执行模型
     *
     * @return 应用执行模型
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected String getLlmModelType(AppContext context) {

        Optional<WorkflowStepWrapper> stepWrapperOptional = Optional.ofNullable(context)
                .map(AppContext::getApp)
                .map(AppEntity::getWorkflowConfig)
                .map(config -> config.getStepWrapperByStepId(context.getStepId()));

        if (!stepWrapperOptional.isPresent()) {
            return null;
        }

        VariableItemEntity modeVariableItem = stepWrapperOptional.get().getModelVariableItem(AppConstants.MODEL);
        if (modeVariableItem == null) {
            return null;
        }

        if (modeVariableItem.getValue() != null) {
            return String.valueOf(modeVariableItem.getValue());
        }

        return null;
    }

    /**
     * 执行结果校验
     *
     * @param actionResponse 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void validateResponse(ActionResponse actionResponse, String stepId) {
        if (actionResponse == null) {
            throw ServiceExceptionUtil.exception0(ErrorCodeConstants.EXECUTE_APP_ACTION_FAILURE.getCode(),
                    "【{}】步骤执行失败: 执行结果不存在，请联系管理员或稍后重试！", stepId);
        }
        // 基本不会走到这里，因为失败都是直接抛出异常，能拿到直接结果，说明已经执行成功！兜底。
        if (!actionResponse.getSuccess()) {
            Integer errorCode = actionResponse.transformErrorCode();
            String errorMessage = actionResponse.transformErrorMessage(actionResponse.getStyle());
            throw ServiceExceptionUtil.exception(new ErrorCode(errorCode, errorMessage));
        }
    }

    /**
     * 权益扣除
     *
     * @param context        上下文
     * @param actionResponse 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void reduceRights(AppContext context, ActionResponse actionResponse) {
        AdminUserRightsTypeEnum userRightsType = this.getUserRightsType();
        Integer costPoints = actionResponse.getCostPoints();
        if (userRightsType != null && costPoints > 0) {
            // 扣除权益
            ReduceRightsDTO reduceRights = new ReduceRightsDTO();
            reduceRights.setUserId(context.getUserId());
            reduceRights.setTeamOwnerId(null);
            reduceRights.setTeamId(null);
            reduceRights.setRightType(userRightsType.getType());
            reduceRights.setReduceNums(costPoints);
            reduceRights.setBizType(UserRightSceneUtils.getUserRightsBizType(context.getScene().name()).getType());
            reduceRights.setBizId(context.getConversationUid());
            ADMIN_USER_RIGHTS_API.reduceRights(reduceRights);
            log.info("步骤节点【扣除权益成功】: 权益类型: {}, 权益点数: {}, 用户ID: {}, 步骤执行器: {}, 步骤ID: {}, 应用UID {}, 会话ID: {}",
                    userRightsType.name(), costPoints, context.getUserId(), this.getClass().getSimpleName(),
                    context.getStepId(), context.getUid(), context.getConversationUid());
            return;
        }
    }

    /**
     * 记录开始日志
     *
     * @param context 上下文
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void loggerBegin(AppContext context, String title) {
        log.info("\n{}【开始执行】: " +
                        "\n\t执行步骤: {}, " +
                        "\n\t步骤执行器: {}, " +
                        "\n\t应用UID: {}, " +
                        "\n\t会话UID: {}, " +
                        "\n\t权益用户: {}, " +
                        "\n\t权益租户: {}, " +
                        "\n\t 来源：{}",
                title,
                context.getStepId(),
                this.getClass().getSimpleName(),
                context.getUid(),
                context.getConversationUid(),
                context.getUserId(),
                TenantContextHolder.getTenantId(),
                context.getApp().getSource()

        );
    }

    /**
     * 记录参数日志
     *
     * @param context 上下文
     * @param param   参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void loggerParamter(AppContext context, Object param, String title) {
        log.info("\n{}【准备调用模型】: " +
                        "\n\t执行步骤: {}, " +
                        "\n\t步骤执行器: {}, " +
                        "\n\t应用UID: {}, " +
                        "\n\t会话UID: {}, " +
                        "\n\t权益用户: {}, " +
                        "\n\t权益租户: {}, " +
                        "\n\t请求参数: {}",
                title,
                context.getStepId(),
                this.getClass().getSimpleName(),
                context.getUid(),
                context.getConversationUid(),
                context.getUserId(),
                TenantContextHolder.getTenantId(),
                JsonUtils.toJsonString(param)
        );
    }

    /**
     * 记录结束日志
     *
     * @param context  上下文
     * @param response 执行结果
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void loggerSuccess(AppContext context, ActionResponse response, String title) {
        log.info("\n{}【执行成功】: " +
                        "\n\t执行步骤: {}, " +
                        "\n\t步骤执行器: {}, " +
                        "\n\t应用UID: {}, " +
                        "\n\t会话UID: {}, " +
                        "\n\t权益用户: {}, " +
                        "\n\t权益租户: {}, " +
                        "\n\t执行结果: {}",
                title,
                context.getStepId(),
                this.getClass().getSimpleName(),
                context.getUid(),
                context.getConversationUid(),
                context.getUserId(),
                TenantContextHolder.getTenantId(),
                JsonUtils.toJsonString(response.getOutput())
        );
    }

    /**
     * 记录错误日志
     *
     * @param context   上下文
     * @param exception 异常
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected void loggerError(AppContext context, Exception exception, String title) {
        Integer errorCode = ErrorCodeConstants.EXECUTE_APP_ACTION_FAILURE.getCode();
        if (exception instanceof ActionResponseException) {
            errorCode = ((ActionResponseException) exception).getCode();
        }
        if (exception instanceof ServiceException) {
            errorCode = ((ServiceException) exception).getCode();
        }
        log.error("\n{}【执行失败】: " +
                        "\n\t执行步骤: {}, " +
                        "\n\t步骤执行器: {}, " +
                        "\n\t应用UID: {}, " +
                        "\n\t会话UID: {}, " +
                        "\n\t权益用户: {}, " +
                        "\n\t权益租户: {}, " +
                        "\n\t异常类型: {}, " +
                        "\n\t错误码: {}, " +
                        "\n\t异常信息: {}",
                title,
                context.getStepId(),
                this.getClass().getSimpleName(),
                context.getUid(),
                context.getConversationUid(),
                context.getUserId(),
                TenantContextHolder.getTenantId(),
                exception.getClass().getSimpleName(),
                errorCode,
                exception.getMessage()
        );

    }

    /**
     * 因为@Data重写了hasCode, equals, 导致子类比较都相等，所以这里改成继承object, 重写equals即可
     *
     * @param obj
     * @return
     * @Data注解在派生子类上时默认@EqualsAndHashCode(callSuper = false)，即重写子类的equals和hashcode不包含父类
     */
    @Override
    public boolean equals(Object obj) {
        boolean equals = super.equals(obj);
        return equals;
    }


}
