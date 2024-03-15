package com.starcloud.ops.business.app.domain.entity.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.starcloud.ops.business.app.api.xhs.scheme.dto.CreativeOptionDTO;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.WorkflowStepEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.action.MaterialActionHandler;
import com.starcloud.ops.business.app.domain.entity.workflow.action.base.BaseActionHandler;
import com.starcloud.ops.business.app.domain.handler.common.BaseHandler;
import com.starcloud.ops.business.app.enums.app.AppStepResponseStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.enums.xhs.CreativeConstants;
import com.starcloud.ops.business.app.enums.xhs.CreativeOptionModelEnum;
import com.starcloud.ops.business.app.util.JsonSchemaUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
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
    public JsonSchema getInVariableJsonSchema() {

        //只是拿到实例，并没有初始化相关上下文
        BaseActionHandler baseActionHandler = BaseActionHandler.of(this.getFlowStep().getHandler());


        JsonSchema jsonSchema = baseActionHandler.getInVariableJsonSchema(this);

        return jsonSchema;
    }


    /**
     * 获取节点的出参结构
     */
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
        Function<VariableItemEntity, Object> consumer = (item) -> ObjectUtil.isEmpty(item.getValue()) ? item.getDefaultValue() : item.getValue();
        String key = VariableEntity.generateKey(prefixKey, this.getStepCode());
        Map<String, Object> variableMap = VariableEntity.mergeVariables(this.variable, this.flowStep.getVariable(), consumer, key);

        variableMap.put(VariableEntity.generateKey(prefixKey, this.getStepCode(), "_OUT"), this.flowStep.getValue());
        variableMap.put(VariableEntity.generateKey(prefixKey, this.getStepCode(), "_DATA"), this.flowStep.getOutput());
        variableMap.put(VariableEntity.generateKey(prefixKey, this.getStepCode(), CreativeConstants.STEP_RESP_JSONSCHEMA), this.flowStep.getOutputJsonSchema());
        return variableMap;

    }

    /**
     * 获取当前步骤的指定变量的 value
     *
     * @return 变量的 values 集合
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public <T> T getContextVariablesValue(String field) {
        Function<VariableItemEntity, Object> consumer = (item) -> ObjectUtil.isEmpty(item.getValue()) ? item.getDefaultValue() : item.getValue();

        Map<String, Object> variableMap = VariableEntity.mergeVariables(this.variable, this.flowStep.getVariable(), consumer, "");

        variableMap.put(VariableEntity.generateKey("_OUT"), this.flowStep.getValue());
        variableMap.put(VariableEntity.generateKey("_DATA"), this.flowStep.getOutput());

        variableMap.put(VariableEntity.generateKey(CreativeConstants.STEP_RESP_JSONSCHEMA), this.flowStep.getOutputJsonSchema());

        return (T) variableMap.getOrDefault(field, null);

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

    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(String key, Object value) {
        this.variable.putVariable(key, value);
    }
}
