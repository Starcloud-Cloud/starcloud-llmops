package com.starcloud.ops.business.app.domain.entity.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.api.verification.Verification;
import com.starcloud.ops.business.app.verification.VerificationUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * App 配置实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuppressWarnings("all")
public class WorkflowConfigEntity extends BaseConfigEntity {

    /**
     * 模版步骤
     */
    private List<WorkflowStepWrapper> steps;

    /**
     * 模版变量
     */
    private VariableEntity variable;

    /**
     * 基础校验模版
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public List<Verification> validate(String uid, ValidateTypeEnum validateType) {
        List<Verification> verifications = new ArrayList<>();
        // 步骤名称重复检查，并且进行步骤校验
        Set<String> stepNameSet = new HashSet<>();
        for (WorkflowStepWrapper stepWrapper : this.stepWrapperList()) {
            if (Objects.isNull(stepWrapper)) {
                continue;
            }
            if (!stepNameSet.add(stepWrapper.getName())) {
                VerificationUtils.addVerificationApp(verifications, uid, "应用步骤【" + stepWrapper.getName() + "】名称重复！");
            }

            // 每个步骤的具体校验
            List<Verification> stepValidationList = stepWrapper.validate(validateType);
            verifications.addAll(stepValidationList);
        }
        return verifications;
    }

    /**
     * 获取所有的步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public List<WorkflowStepWrapper> stepWrapperList() {
        return CollectionUtil.emptyIfNull(this.getSteps()).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 模版步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapper getFirstStepWrapper() {
        return CollectionUtil.emptyIfNull(steps).stream()
                .findFirst()
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_STEPS_REQUIRED));
    }

    /**
     * 根据 StepId 获取指定步骤
     *
     * @param stepId 步骤ID
     * @return 指定步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapper getStepWrapper(String stepId) {
        return CollectionUtil.emptyIfNull(steps).stream()
                .filter(item -> item.getStepCode().equalsIgnoreCase(stepId) || item.getName().equalsIgnoreCase(stepId))
                .findFirst()
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_STEP_NON_EXISTENT, stepId));
    }

    /**
     * 根据 StepId 获取指定步骤
     *
     * @param stepId 步骤ID
     * @return 指定步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapper getStepWrapperByStepId(String stepId) {
        return CollectionUtil.emptyIfNull(steps).stream()
                .filter(item -> item.getStepCode().equalsIgnoreCase(stepId) || item.getName().equalsIgnoreCase(stepId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据 actionHandler 获取指定步骤
     *
     * @param stepId 步骤ID
     * @return 指定步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapper getStepWrapper(Class<? extends BaseActionHandler> classz) {
        String handler = classz.getSimpleName();

        return CollectionUtil.emptyIfNull(steps).stream()
                .filter(item -> item.getHandler().equals(handler))
                .findFirst()
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_STEP_NON_EXISTENT, handler));
    }

    /**
     * 根据 actionHandler 获取指定步骤
     *
     * @param classz
     * @return
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapper getStepWrapperWithoutError(Class<? extends BaseActionHandler> classz) {
        String handler = classz.getSimpleName();

        return CollectionUtil.emptyIfNull(steps).stream()
                .filter(item -> item.getHandler().equals(handler))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取指定步骤之前的所有步骤，包括指定步骤
     *
     * @param stepId 步骤ID
     * @return 指定步骤之前的所有步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public List<WorkflowStepWrapper> getPreStepWrappers(String stepId) {
        List<WorkflowStepWrapper> preStepList = new ArrayList<>();
        for (WorkflowStepWrapper wrapper : steps) {
            if (!wrapper.getStepCode().equalsIgnoreCase(stepId) || !wrapper.getName().equalsIgnoreCase(stepId)) {
                preStepList.add(wrapper);
            } else {
                preStepList.add(wrapper);
                break;
            }
        }
        return preStepList;
    }

    /**
     * 判断处理器是否只有一个
     *
     * @param clazz 处理器
     * @return 处理器是否只有一个
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isOnlyoneHandler(Class<? extends BaseActionHandler> clazz) {
        return this.stepWrapperList().stream()
                .filter(item -> item.equalsHandler(clazz))
                .count() == 1;
    }

    /**
     * 执行成功后，响应更新
     *
     * @param stepId   步骤ID
     * @param response 响应
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void setActionResponse(String stepId, ActionResponse response) {
        if (StringUtils.isBlank(stepId) || Objects.isNull(response)) {
            return;
        }

        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (stepId.equalsIgnoreCase(step.getStepCode()) || stepId.equalsIgnoreCase(step.getName())) {
                step.setActionResponse(response);
            }
        }
    }

    /**
     * 根据变量的{@code field}获取变量，找不到时返回{@code null}
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getVariableItem(String stepId, String field) {
        if (StringUtils.isBlank(stepId) || StringUtils.isBlank(field)) {
            return null;
        }
        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (stepId.equalsIgnoreCase(step.getStepCode()) || stepId.equalsIgnoreCase(step.getName())) {
                return step.getVariableItem(field);
            }
        }
        return null;
    }

    /**
     * 根据变量的{@code field}获取变量，找不到时返回{@code null}
     *
     * @param clazz 节点执行器
     * @param field 变量的{@code field}
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getVariableItem(Class<? extends BaseActionHandler> clazz, String field) {
        if (Objects.isNull(clazz) || StringUtils.isBlank(field)) {
            return null;
        }
        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (clazz.getSimpleName().equalsIgnoreCase(step.getHandler())) {
                return step.getVariableItem(field);
            }
        }
        return null;
    }

    /**
     * 根据变量的{@code field}获取变量的值，找不到时返回null
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getVariable(String stepId, String field) {
        if (StringUtils.isBlank(stepId) || StringUtils.isBlank(field)) {
            return null;
        }
        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (stepId.equalsIgnoreCase(step.getStepCode()) || stepId.equalsIgnoreCase(step.getName())) {
                return step.getVariable(field);
            }
        }
        return null;
    }

    /**
     * 根据变量的{@code field}获取变量的值，找不到时返回null
     *
     * @param clazz 节点执行器
     * @param field 变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getVariable(Class<? extends BaseActionHandler> clazz, String field) {
        if (Objects.isNull(clazz) || StringUtils.isBlank(field)) {
            return null;
        }
        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (clazz.getSimpleName().equalsIgnoreCase(step.getHandler())) {
                return step.getVariable(field);
            }
        }
        return null;
    }

    /**
     * 将变量为{@code field}的值设置为{@code value}
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @param value  变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(String stepId, String field, Object value) {
        if (StringUtils.isBlank(stepId) || StringUtils.isBlank(field)) {
            return;
        }
        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (stepId.equalsIgnoreCase(step.getStepCode()) || stepId.equalsIgnoreCase(step.getName())) {
                step.putVariable(field, value);
            }
        }
    }

    /**
     * 将{@code Map}中的变量值设置到变量中
     *
     * @param clazz 步骤ID
     * @param field 变量的{@code field}
     * @param value 变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(Class<? extends BaseActionHandler> clazz, String field, Object value) {
        if (Objects.isNull(clazz) || StringUtils.isBlank(field)) {
            return;
        }
        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (step.getHandler().equals(clazz.getSimpleName())) {
                step.putVariable(field, value);
            }
        }
    }

    /**
     * 根据变量的{@code field}获取模型变量，找不到时返回{@code null}
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getModelVariableItem(String stepId, String field) {
        if (StringUtils.isBlank(stepId) || StringUtils.isBlank(field)) {
            return null;
        }
        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (stepId.equalsIgnoreCase(step.getStepCode()) || stepId.equalsIgnoreCase(step.getName())) {
                return step.getModelVariableItem(field);
            }
        }
        return null;
    }

    /**
     * 根据变量的{@code field}获取模型变量，找不到时返回{@code null}
     *
     * @param clazz 节点执行器
     * @param field 变量的{@code field}
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getModelVariableItem(Class<? extends BaseActionHandler> clazz, String field) {
        if (Objects.isNull(clazz) || StringUtils.isBlank(field)) {
            return null;
        }
        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (clazz.getSimpleName().equalsIgnoreCase(step.getHandler())) {
                return step.getModelVariableItem(field);
            }
        }
        return null;
    }

    /**
     * 根据变量的{@code field}获取模型变量的值，找不到时返回null
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getModelVariable(String stepId, String field) {
        if (StringUtils.isBlank(stepId) || StringUtils.isBlank(field)) {
            return null;
        }
        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (stepId.equalsIgnoreCase(step.getStepCode()) || stepId.equalsIgnoreCase(step.getName())) {
                return step.getModelVariable(field);
            }
        }
        return null;
    }

    /**
     * 根据变量的{@code field}获取模型变量的值，找不到时返回null
     *
     * @param clazz 节点执行器
     * @param field 变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getModelVariable(Class<? extends BaseActionHandler> clazz, String field) {
        if (Objects.isNull(clazz) || StringUtils.isBlank(field)) {
            return null;
        }
        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (clazz.getSimpleName().equalsIgnoreCase(step.getHandler())) {
                return step.getModelVariable(field);
            }
        }
        return null;
    }

    /**
     * 将模型变量为{@code field}的值设置为{@code value}
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @param value  变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putModelVariable(String stepId, String field, Object value) {
        if (StringUtils.isBlank(stepId) || StringUtils.isBlank(field)) {
            return;
        }
        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (stepId.equalsIgnoreCase(step.getStepCode()) || stepId.equalsIgnoreCase(step.getName())) {
                step.putModelVariable(field, value);
            }
        }
    }

    /**
     * 将{@code Map}中的变量值设置到模型变量中
     *
     * @param clazz 步骤ID
     * @param field 变量的{@code field}
     * @param value 变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putModelVariable(Class<? extends BaseActionHandler> clazz, String field, Object value) {
        if (Objects.isNull(clazz) || StringUtils.isBlank(field)) {
            return;
        }
        for (WorkflowStepWrapper step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (step.getHandler().equals(clazz.getSimpleName())) {
                step.putModelVariable(field, value);
            }
        }
    }

    /**
     * 添加变量
     *
     * @param stepId 步骤ID
     * @param key    变量的{@code field}
     * @param value  变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void addVariable(String stepId, String key, Object value) {
        for (WorkflowStepWrapper step : this.steps) {
            if (step.getStepCode().equalsIgnoreCase(stepId) || step.getName().equalsIgnoreCase(stepId)) {
                step.addVariable(key, value);
            }
        }
    }
}
