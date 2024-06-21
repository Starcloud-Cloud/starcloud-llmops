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
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void supplementStepVariable(Map<String, VariableRespVO> variableRespVOMap) {
        try {
            if (CollectionUtil.isEmpty(steps) || CollectionUtil.isEmpty(variableRespVOMap)) {
                return;
            }
            for (WorkflowStepWrapperRespVO step : steps) {
                step.supplementStepVariable(variableRespVOMap);
            }
        } catch (Exception e) {
            log.warn("supplementStepVariable error", e);
        }
    }

    /**
     * 向step 中添加变量
     *
     * @param stepId   步骤ID
     * @param variable 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putVariable(String stepId, Map<String, Object> variable) {
        for (WorkflowStepWrapperRespVO step : steps) {
            if (stepId.equals(step.getField())) {
                step.putVariable(variable);
                break;
            }
        }
    }

    /**
     * 获取步骤中的变量
     *
     * @param stepId       步骤ID
     * @param variableName 变量名称
     * @return 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public VariableItemRespVO getStepVariable(String stepId, String variableName) {
        for (WorkflowStepWrapperRespVO step : steps) {
            if (stepId.equals(step.getField())) {
                return step.getVariable(variableName);
            }
        }
        return null;
    }

    /**
     * 获取步骤中的变量值
     *
     * @param stepId       步骤ID
     * @param variableName 变量名称
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getStepVariableValue(String stepId, String variableName) {
        for (WorkflowStepWrapperRespVO step : steps) {
            if (stepId.equals(step.getField())) {
                return step.getStepVariableValue(variableName);
            }
        }
        return null;
    }

    /**
     * 向step 中添加变量
     *
     * @param stepId   步骤ID
     * @param variable 变量
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void putStepModelVariable(String stepId, Map<String, Object> variable) {
        for (WorkflowStepWrapperRespVO step : steps) {
            if (stepId.equals(step.getField())) {
                step.putStepModelVariable(variable);
                break;
            }
        }
    }

    /**
     * 获取步骤中的变量值
     *
     * @param stepId       步骤ID
     * @param variableName 变量名称
     * @return 变量值
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public String getStepModelVariableValue(String stepId, String variableName) {
        for (WorkflowStepWrapperRespVO step : steps) {
            if (stepId.equals(step.getField())) {
                return step.getStepModelVariableValue(variableName);
            }
        }
        return null;
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
        for (WorkflowStepWrapperRespVO step : steps) {
            if (handler.equals(step.getFlowStep().getHandler())) {
                return step;
            }
        }
        return null;
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
                .collect(Collectors.toMap(WorkflowStepWrapperRespVO::getField, Function.identity()));

        List<WorkflowStepWrapperRespVO> mergeStepWrapperList = new ArrayList<>();

        // 循环处理
        for (WorkflowStepWrapperRespVO step : this.steps) {
            // 如果map中不存在，或者为空，则不进行合并处理，直接放到新的list中
            if (!stepWrapperMap.containsKey(step.getField()) ||
                    Objects.isNull(stepWrapperMap.get(step.getField()))) {
                mergeStepWrapperList.add(step);
                continue;
            }

            // 进行合并处理
            WorkflowStepWrapperRespVO stepWrapper = stepWrapperMap.get(step.getField());
            step.merge(stepWrapper);

            // 将合并后的数据放到新的list中
            mergeStepWrapperList.add(step);
        }

        this.steps = mergeStepWrapperList;
    }

    /**
     * 应用配置设置
     *
     * @param simpleName         类名
     * @param handlerStepWrapper 步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public void setStepByHandler(String simpleName, WorkflowStepWrapperRespVO handlerStepWrapper) {
        List<WorkflowStepWrapperRespVO> stepList = new ArrayList<>();
        for (WorkflowStepWrapperRespVO step : steps) {
            if (handlerStepWrapper.getField().equals(step.getField()) && simpleName.equals(step.getFlowStep().getHandler())) {
                stepList.add(handlerStepWrapper);
            } else {
                stepList.add(step);
            }
        }
        this.steps = stepList;
    }
}
