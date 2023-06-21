package com.starcloud.ops.business.app.domain.entity.config;

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
    public WorkflowStepWrapper getFirstStep() {

        return Optional.ofNullable(steps).orElse(new ArrayList<>()).stream().findFirst().orElse(null);
    }

    /**
     * 模版步骤
     */
    public WorkflowStepWrapper getStepWrapper(String stepId) {
        return Optional.ofNullable(steps).orElse(new ArrayList<>()).stream().filter((wrapper) -> {
            return wrapper.getField().equals(stepId);
        }).findFirst().orElse(null);
    }

    @Override
    public void validate() {

    }

    public void validateStep() {

        this.validate();

    }
}
