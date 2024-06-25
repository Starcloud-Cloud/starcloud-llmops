package com.starcloud.ops.business.app.domain.entity.workflow.action.base;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.kstry.framework.core.annotation.ReqTaskParam;
import cn.kstry.framework.core.bus.ScopeDataOperator;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.domain.cache.AppStepStatusCache;
import com.starcloud.ops.business.app.domain.entity.AppEntity;
import com.starcloud.ops.business.app.domain.entity.BaseAppEntity;
import com.starcloud.ops.business.app.domain.entity.config.WorkflowStepWrapper;
import com.starcloud.ops.business.app.domain.entity.params.JsonData;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.JsonDataDefSchema;
import com.starcloud.ops.business.app.domain.entity.workflow.WorkflowStepEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.context.AppContext;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import com.starcloud.ops.business.app.util.UserRightSceneUtils;
import com.starcloud.ops.business.app.workflow.app.process.AppProcessParser;
import com.starcloud.ops.business.user.api.rights.AdminUserRightsApi;
import com.starcloud.ops.business.user.api.rights.dto.ReduceRightsDTO;
import com.starcloud.ops.business.user.enums.rights.AdminUserRightsTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
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
     * 具体handler的入参定义
     *
     * @return
     */
    public JsonSchema getInVariableJsonSchema(WorkflowStepWrapper workflowStepWrapper) {
        //默认所有节点的入参都不返回支持
        return null;
    }


    /**
     * 具体handler的出参定义
     *
     * @return
     */
    public JsonSchema getOutVariableJsonSchema(WorkflowStepWrapper workflowStepWrapper) {
        //如果配置了返回结构定义就获取，不然就创建一个默认的
        String json = Optional.of(workflowStepWrapper.getFlowStep()).map(WorkflowStepEntity::getResponse).map(ActionResponse::getOutput).map(JsonData::getJsonSchema).orElse("");
        if (StrUtil.isNotBlank(json)) {
            //有配置，直接返回
            JsonSchema jsonSchema = JsonSchemaUtils.str2JsonSchema(json);
            return jsonSchema;
        } else {
            //定义一个默认的JsonSchema结构， xxx.data
            return JsonSchemaUtils.generateJsonSchema(JsonDataDefSchema.class);
        }
    }


    /**
     * 具体当前handler的出参定义
     *
     * @return
     */
    public JsonSchema getOutVariableJsonSchema(AppContext context) {

        WorkflowStepWrapper workflowStepWrapper = context.getStepWrapper(context.getStepId());
        return this.getOutVariableJsonSchema(workflowStepWrapper);
    }

    /**
     * 判断师傅配置了返回结果为JsonSchema
     *
     * @param workflowStepWrapper
     * @return
     */
    public Boolean hasResponseJsonSchema(WorkflowStepWrapper workflowStepWrapper) {
        //如果配置了返回结构定义就获取，不然就创建一个默认的
        String json = Optional.of(workflowStepWrapper.getFlowStep()).map(WorkflowStepEntity::getResponse).map(ActionResponse::getOutput).map(JsonData::getJsonSchema).orElse("");
        return StrUtil.isNotBlank(json);
    }

    /**
     * 判断师傅配置了返回结果为JsonSchema
     *
     * @param workflowStepWrapper
     * @return
     */
    public Boolean hasResponseJsonSchema(AppContext context) {

        WorkflowStepWrapper workflowStepWrapper = context.getStepWrapper(context.getStepId());
        return this.hasResponseJsonSchema(workflowStepWrapper);
    }

    /**
     * 获取应用的UID
     *
     * @return 应用的UID
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected String getAppUid(AppContext context) {
        return Optional.ofNullable(context)
                .map(AppContext::getApp)
                .map(BaseAppEntity::getUid)
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.APP_UID_REQUIRED));
    }

    /**
     * 获取应用执行模型
     *
     * @return 应用执行模型
     */
    @JsonIgnore
    @JSONField(serialize = false)
    protected String getAiModel(AppContext context) {
        String aiModel = context.getAiModel();
        if (aiModel != null) {
            return aiModel;
        }

        Optional<WorkflowStepWrapper> stepWrapperOptional = Optional.ofNullable(context)
                .map(AppContext::getApp)
                .map(AppEntity::getWorkflowConfig)
                .map(config -> config.getStepWrapperByStepId(context.getStepId()));
        if (!stepWrapperOptional.isPresent()) {
            return null;
        }

        VariableItemEntity modeVariableItem = stepWrapperOptional.get().getModeVariableItem("MODEL");
        if (modeVariableItem == null) {
            return null;
        }

        if (modeVariableItem.getValue() != null) {
            return String.valueOf(modeVariableItem.getValue());
        }

        if (modeVariableItem.getDefaultValue() != null) {
            return String.valueOf(modeVariableItem.getDefaultValue());
        }

        return null;
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

        // 从工作流上下文中获取步骤ID
        Optional<String> property = scopeDataOperator.getTaskProperty();
        AppProcessParser.ServiceTaskPropertyDTO serviceTaskProperty = JSONUtil.toBean(property.get(), AppProcessParser.ServiceTaskPropertyDTO.class);
        String stepId = serviceTaskProperty.getStepId();
        String appUid = this.getAppUid(context);

        try {

            if (Objects.isNull(context)) {
                throw ServiceExceptionUtil.exception(ErrorCodeConstants.APP_CONTEXT_REQUIRED);
            }

            // 更新缓存为开始
            appStepStatusCache.stepStart(context.getConversationUid(), context.getStepId());

            // 执行前的准备工作
            context.setStepId(stepId);

            // 执行具体的步骤, 传入上下文，避免多线层的影响
            ActionResponse actionResponse = this.doExecute(context);

            // 将执行结果设置到上下文中
            context.setActionResponse(actionResponse);

            // 执行结果校验, 如果失败，抛出异常
            validateResponse(actionResponse);

            // 权益扣除
            reduceRights(context, actionResponse, stepId, appUid);

            // 更新缓存为成功
            appStepStatusCache.stepSuccess(context.getConversationUid(), context.getStepId());

            log.info("步骤节点【执行成功】: 步骤执行器: {}, 步骤ID: {}, 应用UID {}, 权益用户: {}, 权益租户: {}",
                    this.getClass().getSimpleName(), stepId, appUid, context.getUserId(), TenantContextHolder.getTenantId());

            return actionResponse;

        } catch (ServiceException exception) {
            // 更新缓存为失败
            appStepStatusCache.stepFailure(context.getConversationUid(), stepId, exception.getCode().toString(), exception.getMessage());
            log.error("步骤节点【执行失败】: 步骤执行器: {}, 步骤ID: {}, 应用UID {}, 权益用户: {}, 权益租户: {}, \n\t错误码: {}, 异常信息: {}",
                    this.getClass().getSimpleName(), context.getStepId(), exception.getCode(), exception.getMessage());

            throw exception;

        } catch (Exception exception) {
            // 更新缓存为失败
            appStepStatusCache.stepFailure(context.getConversationUid(), stepId, ErrorCodeConstants.EXECUTE_APP_FAILURE.getCode().toString(), exception.getMessage());
            log.error("步骤节点【执行失败】: 步骤执行器: {}, 步骤ID: {}, 应用UID {}, 权益用户: {}, 权益租户: {}, \n\t异常信息: {}",
                    this.getClass().getSimpleName(), context.getStepId(), exception.getMessage());

            throw exception;
        }
    }

    /**
     * 执行结果校验
     *
     * @param actionResponse 执行结果
     */
    private static void validateResponse(ActionResponse actionResponse) {

        if (actionResponse == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_FAILURE);
        }

//        if (!actionResponse.getSuccess()) {
//            String errorCode = StringUtils.isNoneBlank(actionResponse.getErrorCode()) ? actionResponse.getErrorCode() : ErrorCodeConstants.EXECUTE_APP_FAILURE.getCode().toString();
//            String errorMsg = StringUtils.isNoneBlank(actionResponse.getErrorMsg()) ? actionResponse.getErrorMsg() : "应用Action执行失败，请稍后重试或者联系管理员！";
//            Integer code;
//            try {
//                code = Integer.parseInt(errorCode);
//            } catch (Exception e) {
//                code = ErrorCodeConstants.EXECUTE_APP_FAILURE.getCode();
//            }
//            throw ServiceExceptionUtil.exception(new ErrorCode(code, errorMsg));
//        }
    }

    /**
     * 权益扣除
     *
     * @param context        上下文
     * @param actionResponse 执行结果
     */
    private void reduceRights(AppContext context, ActionResponse actionResponse, String stepId, String appUid) {
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
                    userRightsType.name(), costPoints, context.getUserId(), this.getClass().getSimpleName(), stepId, appUid, context.getConversationUid());
            return;
        }
        log.info("步骤节点【无需扣除权益】: 权益类型: {}, 权益点数: {}, 用户ID: {}, 步骤执行器: {}, 步骤ID: {}, 应用UID {}, 会话ID: {}",
                userRightsType.name(), costPoints, context.getUserId(), this.getClass().getSimpleName(), stepId, appUid, context.getConversationUid());
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
