package com.starcloud.ops.business.app.domain.entity.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * App 配置实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
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
     * 模版步骤
     */
    @JSONField(serialize = false)
    public WorkflowStepWrapper getFirstStep() {

        return Optional.ofNullable(steps).orElse(new ArrayList<>()).stream().findFirst().orElse(null);
    }

    /**
     * 模版步骤
     */
    @JSONField(serialize = false)
    public WorkflowStepWrapper getStepWrapper(String stepId) {
        return Optional.ofNullable(steps).orElse(new ArrayList<>()).stream().filter((wrapper) -> {
            return wrapper.getField().equals(stepId);
        }).findFirst().orElse(null);
    }


    /**
     * 获取指定步骤之前的所有步骤
     */
    @JSONField(serialize = false)
    public List<WorkflowStepWrapper> getPreStepWrappers(String stepId) {

        List<WorkflowStepWrapper> preWorkflowStepWrappers = new ArrayList<>();

        for (WorkflowStepWrapper wrapper : steps) {

            if (!wrapper.getField().equals(stepId)) {
                preWorkflowStepWrappers.add(wrapper);
            } else {
                preWorkflowStepWrappers.add(wrapper);
                break;
            }
        }

        return preWorkflowStepWrappers;
    }

    @Override
    public void validate() {

    }

    public void validateStep() {

        this.validate();

    }
}
