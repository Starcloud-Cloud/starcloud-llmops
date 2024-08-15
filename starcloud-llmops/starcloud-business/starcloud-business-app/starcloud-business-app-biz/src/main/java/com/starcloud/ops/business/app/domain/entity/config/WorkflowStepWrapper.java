package com.starcloud.ops.business.app.domain.entity.config;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.api.AppValidate;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.WorkflowStepEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.VariableDefInterface;
import com.starcloud.ops.business.app.enums.ValidateTypeEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * App 步骤实体包装类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class WorkflowStepWrapper {

    /**
     * 步骤 field
     */
    @Deprecated
    private String field;

    /**
     * 步骤label
     */
    private String name;

    /**
     * 步骤按钮label
     */
    private String buttonLabel;

    /**
     * 步骤描述
     */
    private String description;

    /**
     * 具体的步骤配置
     */
    private WorkflowStepEntity flowStep;

    /**
     * 提示词变量
     */
    private VariableEntity variable;

    /**
     * 基础校验模版
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate(ValidateTypeEnum validateType) {
        AppValidate.notBlank(this.name, "应用步骤名称不能为空");
        AppValidate.notNull(this.flowStep, "应用步骤配置不能为空");
        AppValidate.notNull(this.variable, "应用步骤变量不能为空");

        this.variable.validate(validateType);
        this.flowStep.validate(validateType);

        // 获取步骤处理器
        BaseActionHandler handler = BaseActionHandler.of(this.getHandler());
        AppValidate.notNull(handler, "应用步骤处理器(" + this.getHandler() + ")不存在或不支持");
        // 校验步骤处理器
        if (Objects.nonNull(handler)) {
            handler.validate(this, validateType);
        }
    }

    /**
     * 获取节点的入参结构
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public JsonSchema getInVariableJsonSchema() {
        //只是拿到实例，并没有初始化相关上下文
        BaseActionHandler baseActionHandler = BaseActionHandler.of(this.getFlowStep().getHandler());

        if (baseActionHandler instanceof VariableDefInterface) {
            VariableDefInterface variableDefInterface = (VariableDefInterface) baseActionHandler;
            JsonSchema jsonSchema = variableDefInterface.inVariableJsonSchema();
            return jsonSchema;
        }

        JsonSchema jsonSchema = baseActionHandler.getInVariableJsonSchema(this);
        return jsonSchema;
    }

    /**
     * 获取节点的出参结构
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public JsonSchema getOutVariableJsonSchema() {
        //只是拿到实例，并没有初始化相关上下文
        BaseActionHandler baseActionHandler = BaseActionHandler.of(this.getFlowStep().getHandler());

        if (baseActionHandler instanceof VariableDefInterface) {
            VariableDefInterface variableDefInterface = (VariableDefInterface) baseActionHandler;
            JsonSchema jsonSchema = variableDefInterface.outVariableJsonSchema();
            return jsonSchema;
        }

        JsonSchema jsonSchema = baseActionHandler.getOutVariableJsonSchema(this);
        return jsonSchema;
    }

    /**
     * 获取当前步骤配置的 步骤 Code
     *
     * @return 步骤Code
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getStepCode() {
        return this.field;
    }

    /**
     * 获取当前步骤的所有变量的 values 集合
     *
     * @return 变量的 values 集合
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Map<String, Object> getContextVariablesValues(String prefixKey) {
        return this.getContextVariablesValues(prefixKey, true);
    }

    /**
     * 获取当前步骤的所有变量的 values 集合
     *
     * @return 变量的 values 集合
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Map<String, Object> getContextVariablesValues(String prefixKey, Boolean hasStepCode) {
        Map<String, Object> variableMap = this.getOriginalVariablesValues(prefixKey, hasStepCode);

        String stepCode = null;
        if (hasStepCode) {
            stepCode = this.getStepCode();
        }

        //@todo 需要把这些后增加的参数，放在执行具体handler之前，每个handler可定义初始化的参数供本身去使用
        variableMap.put(VariableEntity.generateKey(prefixKey, stepCode, CreativeConstants.STEP_RESP_JSONSCHEMA), JsonSchemaUtils.jsonSchema2Str(this.getOutVariableJsonSchema()));
        return variableMap;

    }

    /**
     * 获取当前步骤的指定变量的 value
     *
     * @return 变量的 values 集合
     */
    @JsonIgnore
    @JSONField(serialize = false)
    @SuppressWarnings("unchecked")
    public <T> T getVariablesValue(String field) {
        Map<String, Object> variableMap = this.getOriginalVariablesValues(null, false);
        return (T) variableMap.getOrDefault(field, null);

    }

    /**
     * 获取当前步骤的所有变量的 values 集合
     *
     * @return 变量的 values 集合
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private Map<String, Object> getOriginalVariablesValues(String prefixKey, Boolean hasStepCode) {
        Function<VariableItemEntity, Object> consumer = (item) -> ObjectUtil.isEmpty(item.getValue()) ? item.getDefaultValue() : item.getValue();

        String stepCode = null;
        if (hasStepCode) {
            stepCode = this.getStepCode();
        }

        String key = VariableEntity.generateKey(prefixKey, stepCode);
        Map<String, Object> variableMap = VariableEntity.mergeVariables(this.variable, this.flowStep.getVariable(), consumer, key);

        variableMap.put(VariableEntity.generateKey(prefixKey, stepCode, "_OUT"), this.flowStep.getValue());
        variableMap.put(VariableEntity.generateKey(prefixKey, stepCode, "_DATA"), this.flowStep.getOutput());

        //新版
        variableMap.put(VariableEntity.generateKey(prefixKey, stepCode), this.flowStep.getOutput());

        return variableMap;

    }

    /**
     * 获取当前步骤的所有变量的 VariableItemEntity 集合
     *
     * @return 变量的 VariableItemEntity 集合
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Map<String, VariableItemEntity> getContextVariableItems() {
        String prefixKey = "STEP." + this.getStepCode();
        return VariableEntity.mergeVariables(this.variable, this.flowStep.getVariable(), (item) -> item, prefixKey);
    }

    /**
     * 根据步骤ID获取响应
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public ActionResponse getActionResponse() {
        return Optional.ofNullable(this.flowStep)
                .map(WorkflowStepEntity::getResponse)
                .orElse(null);
    }

    /**
     * 响应更新
     *
     * @param response 响应
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void setActionResponse(ActionResponse response) {
        ActionResponse actionResponse = this.getActionResponse();
        response.setType(Optional.ofNullable(actionResponse).map(ActionResponse::getType).orElse(AppStepResponseTypeEnum.TEXT.name()));
        response.setStyle(Optional.ofNullable(actionResponse).map(ActionResponse::getStyle).orElse(AppStepResponseStyleEnum.TEXTAREA.name()));
        response.setIsShow(Optional.ofNullable(actionResponse).map(ActionResponse::getIsShow).orElse(Boolean.TRUE));
        this.flowStep.setResponse(response);
    }

    /**
     * 获取步骤执行器
     *
     * @return 步骤执行器
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getHandler() {
        return Optional.ofNullable(this.flowStep).map(WorkflowStepEntity::getHandler).orElse(null);
    }

    /**
     * 根据变量的{@code field}获取变量，找不到时返回{@code null}
     *
     * @param field 变量的{@code field}
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getVariableItem(String field) {
        if (Objects.isNull(variable)) {
            return null;
        }
        return variable.getItem(field);
    }

    /**
     * 根据变量的{@code field}获取变量的值，并且将值转换为字符串，找不到时返回空字符串
     *
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getVariableToString(String field) {
        if (Objects.isNull(variable)) {
            return null;
        }
        return variable.getVariableToString(field);
    }

    /**
     * 根据变量的{@code field}获取变量的值，找不到时返回null
     *
     * @param field 变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getVariable(String field) {
        if (Objects.isNull(variable)) {
            return null;
        }
        return variable.getVariable(field);
    }

    /**
     * 将变量为{@code field}的值设置为{@code value}
     *
     * @param field 变量的{@code field}
     * @param value 变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(String field, Object value) {
        if (Objects.isNull(variable)) {
            return;
        }
        variable.putVariable(field, value);
    }

    /**
     * 根据变量的{@code field}获取模型变量，找不到时返回{@code null}
     *
     * @param field 变量的{@code field}
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getModelVariableItem(String field) {
        if (Objects.isNull(flowStep)) {
            return null;
        }
        return flowStep.getModelVariableItem(field);
    }

    /**
     * 根据模型变量的{@code field}获取变量的值，并且将值转换为字符串，找不到时返回空字符串
     *
     * @param field 变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getModelVariableToString(String field) {
        if (Objects.isNull(this.flowStep)) {
            return StringUtils.EMPTY;
        }
        return this.flowStep.getModelVariableToString(field);
    }

    /**
     * 根据变量的{@code field}获取模型变量的值，找不到时返回null
     *
     * @param field 变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getModelVariable(String field) {
        if (Objects.isNull(flowStep)) {
            return null;
        }
        return flowStep.getModelVariable(field);
    }

    /**
     * 将模型变量为{@code field}的值设置为{@code value}
     *
     * @param field 变量的{@code field}
     * @param value 变量的值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putModelVariable(String field, Object value) {
        if (Objects.isNull(flowStep)) {
            return;
        }
        flowStep.putModelVariable(field, value);
    }

    /**
     * 添加一个变量
     *
     * @param key   key
     * @param value value
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void addVariable(String key, Object value) {
        this.variable.addVariable(key, value);
    }
}
