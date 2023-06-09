package com.starcloud.ops.business.app.domain.entity.config;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.starcloud.ops.business.app.domain.entity.action.WorkflowStepEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * App 步骤实体包装类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class WorkflowStepWrapper {

    /**
     * 步骤 field
     */
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
     * 获取当前步骤的变量值
     *
     * @return
     */
    @JSONField(serialize = false)
    public Object getContextVariablesValue(String field) {

        Map<String, Object> variables = this.getContextVariablesValues(null);

        return variables.get(VariableEntity.generateKey(this.getField(), field));
    }

    /**
     * 获取当前步骤的变量值
     *
     * @return
     */
    @JSONField(serialize = false)
    public <T> T getContextVariablesValue(String key, T def) {

        return def;
    }

    /**
     * 获取当前步骤的所有变量的values
     *
     * @return
     */
    @JSONField(serialize = false)
    public Map<String, Object> getContextVariablesValues(String prefixKey) {


        Map<String, Object> variables = VariableEntity.coverMergeVariables(this.variable, this.flowStep.getVariable(), (variableItemEntity) -> {
            return !ObjectUtil.isEmpty(variableItemEntity.getValue()) ? variableItemEntity.getValue() : variableItemEntity.getDefaultValue();
        }, VariableEntity.generateKey(prefixKey, this.getField()));

        variables.put(VariableEntity.generateKey(prefixKey, this.getField(), "_OUT"), this.flowStep.getValue());

        return variables;

    }

    /**
     * 获取当前步骤的所有变量的Labels形式
     *
     * @return
     */
    @JSONField(serialize = false)
    public Map<String, String> getContextVariablesLabels(String prefixKey) {

        return VariableEntity.coverMergeVariables(this.variable, this.flowStep.getVariable(), (variableItemEntity) -> {
            return variableItemEntity.getLabel();
        }, prefixKey);
    }


    /**
     * 获取当前步骤的变量Keys列表
     *
     * @return
     */
    @JSONField(serialize = false)
    public Set<String> getContextVariablesKeys() {


        return null;
    }


}
