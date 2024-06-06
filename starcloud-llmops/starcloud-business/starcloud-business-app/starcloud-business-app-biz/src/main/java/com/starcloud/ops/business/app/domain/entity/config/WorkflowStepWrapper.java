package com.starcloud.ops.business.app.domain.entity.config;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.WorkflowStepEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
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
     * 获取节点的入参结构
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public JsonSchema getInVariableJsonSchema() {

        //只是拿到实例，并没有初始化相关上下文
        BaseActionHandler baseActionHandler = BaseActionHandler.of(this.getFlowStep().getHandler());


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

        //区分类型，普通节点
        JsonSchema jsonSchema = baseActionHandler.getOutVariableJsonSchema(this);

        return jsonSchema;
    }

    /**
     * 基础校验模版
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate() {
        //this.field = AppUtils.obtainField(this.name);
        this.flowStep.validate();
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
        String prefixKey = "STEP." + this.getField();
        return VariableEntity.mergeVariables(this.variable, this.flowStep.getVariable(), (item) -> item, prefixKey);
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
        if (StringUtils.equalsIgnoreCase(this.name, stepId) || StringUtils.equalsIgnoreCase(this.field, stepId)) {
            ActionResponse actionResponse = this.flowStep.getResponse();

            response.setType(Optional.ofNullable(actionResponse).map(ActionResponse::getType).orElse(AppStepResponseTypeEnum.TEXT.name()));
            response.setStyle(Optional.ofNullable(actionResponse).map(ActionResponse::getStyle).orElse(AppStepResponseStyleEnum.TEXTAREA.name()));
            response.setIsShow(Optional.ofNullable(actionResponse).map(ActionResponse::getIsShow).orElse(Boolean.TRUE));

            this.flowStep.setResponse(response);
        }
    }

    /**
     * 获取模型变量
     *
     * @param key key
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemEntity getModeVariableItem(String key) {
        return this.flowStep.getModeVariableItem(key);
    }

    /**
     * 将变量放入步骤变量中
     *
     * @param key   key
     * @param value value
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(String key, Object value) {
        this.variable.putVariable(key, value);
    }

    /**
     * 将变量放入步骤变量中
     *
     * @param key   key
     * @param value value
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putModelVariable(String key, Object value) {
        this.flowStep.putModelVariable(key, value);
    }
}
