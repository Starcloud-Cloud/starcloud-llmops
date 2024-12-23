package com.starcloud.ops.business.app.api.app.vo.response.config;

import cn.hutool.core.collection.CollectionUtil;
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
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
     * 补充步骤默认变量
     *
     * @param supplementStepWrapperMap 补充步骤包装 map
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void supplementStepVariable(Map<String, WorkflowStepWrapperRespVO> supplementStepWrapperMap) {

        if (Objects.isNull(flowStep) || Objects.isNull(variable) || CollectionUtil.isEmpty(supplementStepWrapperMap)) {
            return;
        }

        String handler = flowStep.getHandler();
        // 获取补充的步骤
        WorkflowStepWrapperRespVO handlerStepWrapper = supplementStepWrapperMap.get(handler);
        if (Objects.isNull(handlerStepWrapper)) {
            return;
        }
        if (!"CustomActionHandler".equals(handler)) {
            this.description = handlerStepWrapper.getDescription();
        }
        flowStep.supplementFlowStep(handlerStepWrapper.getFlowStep());
        variable.supplementStepVariable(handlerStepWrapper.getVariable());
    }

    /**
     * 获取步骤的 code
     *
     * @return String
     */
    public String getStepCode() {
        return this.field;
    }

    /**
     * 获取步骤的执行器
     *
     * @return VariableItemRespVO
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getHandler() {
        return Optional.ofNullable(this.flowStep).map(WorkflowStepRespVO::getHandler).orElse(null);
    }

    /**
     * 获取步骤变量
     *
     * @param field 变量的{@code field}
     * @return VariableItemRespVO
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO getVariableItem(String field) {
        if (Objects.isNull(this.variable)) {
            return null;
        }
        return this.variable.getItem(field);
    }

    /**
     * 根据变量的{@code field}获取变量的值，并且将值转换为字符串，找不到时返回空字符串
     *
     * @param field 变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getVariableToString(String field) {
        if (Objects.isNull(this.variable)) {
            return StringUtils.EMPTY;
        }
        return this.variable.getVariableToString(field);
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
        if (Objects.isNull(this.variable)) {
            return null;
        }
        return this.variable.getVariable(field);
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
        if (Objects.isNull(this.variable)) {
            return;
        }
        this.variable.putVariable(field, value);
    }

    /**
     * 根据模型变量的{@code field}获取变量的值，找不到时返回null
     *
     * @param field 变量的{@code field}
     * @return VariableItemRespVO
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO getModelVariableItem(String field) {
        if (Objects.isNull(this.flowStep)) {
            return null;
        }
        return this.flowStep.getModelVariableItem(field);
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
     * 根据模型变量的{@code field}获取变量的值，找不到时返回null
     *
     * @param field 变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public Object getModelVariable(String field) {
        if (Objects.isNull(this.flowStep)) {
            return null;
        }
        return this.flowStep.getModelVariable(field);
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
        if (Objects.isNull(this.flowStep)) {
            return;
        }
        this.flowStep.putModelVariable(field, value);
    }

    /**
     * 合并步骤
     *
     * @param stepWrapper 步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void merge(WorkflowStepWrapperRespVO stepWrapper) {
        // 只进行变量合并，不进行其他属性合并，
        // step 中的内容保持为最新的。直接抛弃旧的内容。
        this.variable.merge(stepWrapper.getVariable());
    }
}
