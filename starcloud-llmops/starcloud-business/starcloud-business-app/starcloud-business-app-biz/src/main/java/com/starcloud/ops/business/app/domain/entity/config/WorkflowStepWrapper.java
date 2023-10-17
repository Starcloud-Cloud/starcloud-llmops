package com.starcloud.ops.business.app.domain.entity.config;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.domain.entity.workflow.WorkflowStepEntity;
import com.starcloud.ops.business.app.enums.app.AppStepResponseStyleEnum;
import com.starcloud.ops.business.app.enums.app.AppStepResponseTypeEnum;
import com.starcloud.ops.business.app.util.AppUtils;
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
     * 步骤变量,执行
     */
    private VariableEntity variable;

    /**
     * 基础校验模版
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate() {
        this.field = AppUtils.obtainField(this.name);
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
        String key = VariableEntity.generateKey(prefixKey, this.getField());
        Map<String, Object> variableMap = VariableEntity.mergeVariables(this.variable, this.flowStep.getVariable(), consumer, key);
        variableMap.put(VariableEntity.generateKey(prefixKey, this.getField(), "_OUT"), this.flowStep.getValue());
        return variableMap;

    }

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
            response.setType(Optional.of(actionResponse).map(ActionResponse::getType).orElse(AppStepResponseTypeEnum.TEXT.name()));
            response.setStyle(Optional.of(actionResponse).map(ActionResponse::getStyle).orElse(AppStepResponseStyleEnum.TEXTAREA.name()));
            response.setIsShow(Optional.of(actionResponse).map(ActionResponse::getIsShow).orElse(Boolean.TRUE));
            this.flowStep.setResponse(response);
        }
    }

    /**
     * 设置模型变量
     *
     * @param key   变量名
     * @param value 变量值
     */
    public void setModelVariable(String key, Object value) {
        this.flowStep.setModelVariable(key, value);
    }
}
