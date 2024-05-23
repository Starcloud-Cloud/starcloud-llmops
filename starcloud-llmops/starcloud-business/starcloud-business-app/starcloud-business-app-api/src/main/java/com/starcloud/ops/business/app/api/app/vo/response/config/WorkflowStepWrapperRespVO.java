package com.starcloud.ops.business.app.api.app.vo.response.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * App 步骤实体包装类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用 action 工作流步骤包装请求对象")
public class WorkflowStepWrapperRespVO implements Serializable {

    private static final long serialVersionUID = -5726185087363176515L;

    /**
     * 步骤 field
     */
    @Schema(description = "步骤 field")
    private String field;

    /**
     * 步骤label
     */
    @Schema(description = "步骤label")
    private String name;

    /**
     * 步骤按钮label
     */
    @Schema(description = "步骤按钮label")
    private String buttonLabel;

    /**
     * 步骤描述
     */
    @Schema(description = "步骤描述")
    private String description;

    /**
     * 具体的步骤配置
     */
    @Schema(description = "具体的步骤配置")
    private WorkflowStepRespVO flowStep;

    /**
     * 步骤变量
     */
    @Schema(description = "步骤变量")
    private VariableRespVO variable;

    /**
     * 添加步骤变量
     *
     * @param variable 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(Map<String, Object> variable) {
        this.variable.putVariable(variable);
    }

    /**
     * 获取步骤变量
     *
     * @param key 变量key
     * @return VariableItemRespVO
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO getVariable(String key) {
        List<VariableItemRespVO> variables = Optional.ofNullable(this.getVariable()).map(VariableRespVO::getVariables).orElse(new ArrayList<>());
        Map<String, VariableItemRespVO> collect = variables.stream().collect(Collectors.toMap(VariableItemRespVO::getField, Function.identity()));
        if (collect.containsKey(key)) {
            return collect.get(key);
        }
        return null;
    }

    /**
     * 获取步骤变量值.
     *
     * @param key 变量key
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getStepVariableValue(String key) {
        List<VariableItemRespVO> variables = Optional.ofNullable(this.getVariable()).map(VariableRespVO::getVariables).orElse(new ArrayList<>());
        Map<String, VariableItemRespVO> collect = variables.stream().collect(Collectors.toMap(VariableItemRespVO::getField, Function.identity()));
        if (collect.containsKey(key)) {
            VariableItemRespVO variable = collect.get(key);
            if (Objects.nonNull(variable) && Objects.nonNull(variable.getValue())) {
                return String.valueOf(variable.getValue());
            }
        }
        return null;
    }

    /**
     * 添加步骤变量
     *
     * @param variable 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putStepModelVariable(Map<String, Object> variable) {
        this.flowStep.putStepModelVariable(variable);
    }

    /**
     * 获取步骤变量值.
     *
     * @param key 变量key
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getStepModelVariableValue(String key) {
        List<VariableItemRespVO> variables = Optional.ofNullable(this.flowStep)
                .map(WorkflowStepRespVO::getVariable)
                .map(VariableRespVO::getVariables)
                .orElse(new ArrayList<>());

        Map<String, VariableItemRespVO> collect = variables.stream().collect(Collectors.toMap(VariableItemRespVO::getField, Function.identity()));
        if (collect.containsKey(key)) {
            VariableItemRespVO variable = collect.get(key);
            if (Objects.nonNull(variable) && Objects.nonNull(variable.getValue())) {
                return String.valueOf(variable.getValue());
            }
        }
        return null;
    }

    /**
     * 合并步骤
     *
     * @param stepWrapper 步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void merge(WorkflowStepWrapperRespVO stepWrapper) {
        // 只进行变量合并，不进行其他属性合并
        this.variable.merge(stepWrapper.getVariable());
    }
}
