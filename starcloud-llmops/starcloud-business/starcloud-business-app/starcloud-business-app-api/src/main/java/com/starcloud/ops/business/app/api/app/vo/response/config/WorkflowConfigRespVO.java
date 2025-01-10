package com.starcloud.ops.business.app.api.app.vo.response.config;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * App 配置实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用工作流函数请求对象")
@Slf4j
public class WorkflowConfigRespVO extends BaseConfigRespVO {

    private static final long serialVersionUID = -4540655599582546170L;

    /**
     * 模版步骤
     */
    @Schema(description = "模版步骤")
    private List<WorkflowStepWrapperRespVO> steps;

    /**
     * 模版变量
     */
    @Schema(description = "模版变量")
    private VariableRespVO variable;

    /**
     * 补充步骤默认变量
     * @param supplementStepWrapperMap 补充步骤默认变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void supplementStepVariable(Map<String, WorkflowStepWrapperRespVO> supplementStepWrapperMap) {
        try {
            if (CollectionUtil.isEmpty(steps) || CollectionUtil.isEmpty(supplementStepWrapperMap)) {
                return;
            }
            for (WorkflowStepWrapperRespVO step : steps) {
                if (Objects.isNull(step)) {
                    continue;
                }
                step.supplementStepVariable(supplementStepWrapperMap);
            }
        } catch (Exception e) {
            log.warn("supplementStepVariable error", e);
        }
    }

    /**
     * 获取步骤列表
     *
     * @return 步骤列表
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public List<WorkflowStepWrapperRespVO> stepWrapperList() {
        return CollectionUtil.emptyIfNull(this.getSteps()).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 根据 handler 获取步骤
     *
     * @param handler 步骤handler
     * @return 步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapperRespVO getStepByHandler(String handler) {
        if (StringUtils.isBlank(handler)) {
            return null;
        }
        for (WorkflowStepWrapperRespVO step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (handler.equalsIgnoreCase(step.getHandler())) {
                return step;
            }
        }
        return null;
    }

    /**
     * 根据 handler 获取步骤
     *
     * @param clazz 步骤handler类
     * @return 步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapperRespVO getStepByHandler(Class<?> clazz) {
        if (Objects.isNull(clazz)) {
            return null;
        }
        return getStepByHandler(clazz.getSimpleName());
    }

    /**
     * 应用配置设置
     *
     * @param handler 处理去
     * @param wrapper 步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void setStepByHandler(String handler, WorkflowStepWrapperRespVO wrapper) {
        if (StringUtils.isBlank(handler) || Objects.isNull(wrapper)) {
            return;
        }
        List<WorkflowStepWrapperRespVO> stepList = new ArrayList<>();
        for (WorkflowStepWrapperRespVO step : stepWrapperList()) {
            if (wrapper.getStepCode().equalsIgnoreCase(step.getStepCode()) &&
                    handler.equalsIgnoreCase(step.getHandler())) {
                stepList.add(wrapper);
            } else {
                stepList.add(step);
            }
        }
        this.steps = stepList;
    }

    /**
     * 应用配置设置
     *
     * @param clazz   类名
     * @param wrapper 步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void setStepByHandler(Class<?> clazz, WorkflowStepWrapperRespVO wrapper) {
        if (Objects.isNull(clazz) || Objects.isNull(wrapper)) {
            return;
        }
        setStepByHandler(clazz.getSimpleName(), wrapper);
    }

    /**
     * 获取步骤变量
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return VariableItemRespVO
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO getVariableItem(String stepId, String field) {
        if (StringUtils.isBlank(stepId) || StringUtils.isBlank(field)) {
            return null;
        }
        for (WorkflowStepWrapperRespVO step : stepWrapperList()) {
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
     * 根据变量的{@code field}获取变量的值，并且将值转换为字符串，找不到时返回空字符串
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getVariableToString(String stepId, String field) {
        if (StringUtils.isBlank(stepId) || StringUtils.isBlank(field)) {
            return StringUtils.EMPTY;
        }
        for (WorkflowStepWrapperRespVO step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (stepId.equalsIgnoreCase(step.getStepCode()) || stepId.equalsIgnoreCase(step.getName())) {
                return step.getVariableToString(field);
            }
        }
        return StringUtils.EMPTY;
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
        for (WorkflowStepWrapperRespVO step : stepWrapperList()) {
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
        for (WorkflowStepWrapperRespVO step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (stepId.equalsIgnoreCase(step.getStepCode()) || stepId.equalsIgnoreCase(step.getName())) {
                step.putVariable(field, value);
                break;
            }
        }
    }

    /**
     * 根据模型变量的{@code field}获取变量的值，找不到时返回null
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return VariableItemRespVO
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO getModelVariableItem(String stepId, String field) {
        if (StringUtils.isBlank(stepId) || StringUtils.isBlank(field)) {
            return null;
        }
        for (WorkflowStepWrapperRespVO step : stepWrapperList()) {
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
     * 根据模型变量的{@code field}获取变量的值，并且将值转换为字符串，找不到时返回空字符串
     *
     * @param stepId 步骤ID
     * @param field  变量的{@code field}
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getModelVariableToString(String stepId, String field) {
        if (StringUtils.isBlank(stepId) || StringUtils.isBlank(field)) {
            return null;
        }
        for (WorkflowStepWrapperRespVO step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (stepId.equalsIgnoreCase(step.getStepCode()) || stepId.equalsIgnoreCase(step.getName())) {
                return step.getModelVariableToString(field);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 根据模型变量的{@code field}获取变量的值，找不到时返回null
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
        for (WorkflowStepWrapperRespVO step : stepWrapperList()) {
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
        for (WorkflowStepWrapperRespVO step : stepWrapperList()) {
            if (Objects.isNull(step)) {
                continue;
            }
            if (stepId.equalsIgnoreCase(step.getStepCode()) || stepId.equalsIgnoreCase(step.getName())) {
                step.putModelVariable(field, value);
                break;
            }
        }
    }

    /**
     * 应用配置合并
     *
     * @param workflowConfig 应用配置信息
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void merge(WorkflowConfigRespVO workflowConfig) {
        // 如果为空，则不进行合并处理
        if (CollectionUtil.isEmpty(this.steps) || CollectionUtil.isEmpty(workflowConfig.getSteps())) {
            return;
        }

        // 将配置信息转换为map
        Map<String, WorkflowStepWrapperRespVO> stepWrapperMap = workflowConfig.getSteps()
                .stream()
                .collect(Collectors.toMap(WorkflowStepWrapperRespVO::getStepCode, Function.identity()));

        List<WorkflowStepWrapperRespVO> mergeStepWrapperList = new ArrayList<>();

        // 循环处理
        for (WorkflowStepWrapperRespVO step : this.steps) {
            // 如果map中不存在，或者为空，则不进行合并处理，直接放到新的list中
            if (!stepWrapperMap.containsKey(step.getStepCode()) || Objects.isNull(stepWrapperMap.get(step.getStepCode()))) {
                mergeStepWrapperList.add(step);
                continue;
            }

            // 进行合并处理
            WorkflowStepWrapperRespVO stepWrapper = stepWrapperMap.get(step.getStepCode());
            step.merge(stepWrapper);
            // 将合并后的数据放到新的list中
            mergeStepWrapperList.add(step);
        }

        this.steps = mergeStepWrapperList;
    }


}
