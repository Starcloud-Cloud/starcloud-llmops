package com.starcloud.ops.business.app.domain.entity2.config;

import com.starcloud.ops.business.app.domain.entity.AppStepWrapper;
import com.starcloud.ops.business.app.domain.entity2.variable.VariableEntity;
import lombok.Data;

import java.util.List;

/**
 * App 配置实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class WorkflowFlowConfigEntity extends BaseConfigEntity {

    /**
     * 模版步骤
     */
    private List<FlowStepWrapper> steps;

    /**
     * 模版变量
     */
    private VariableEntity variable;


    /**
     * 模版步骤
     */
    public AppStepWrapper getFirstStep() {
        return null;
    }

    /**
     * 模版步骤
     */
    public AppStepWrapper getStep(String stepId) {
        return null;
    }

    @Override
    public void validate() {

    }

    public void validateStep() {

        this.validate();

    }
}
