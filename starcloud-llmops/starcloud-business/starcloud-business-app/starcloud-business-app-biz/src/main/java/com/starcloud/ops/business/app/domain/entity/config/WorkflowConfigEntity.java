package com.starcloud.ops.business.app.domain.entity.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.workflow.ActionResponse;
import com.starcloud.ops.business.app.enums.ErrorCodeConstants;
import com.starcloud.ops.business.app.validate.AppValidate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * App 配置实体类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuppressWarnings("all")
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
     * 基础校验模版
     */
    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public void validate() {
        AppValidate.notEmpty(this.steps, ErrorCodeConstants.EXECUTE_APP_STEPS_REQUIRED);
    }

    /**
     * 模版步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapper getFirstStepWrapper() {
        return CollectionUtil.emptyIfNull(steps).stream()
                .findFirst()
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_STEPS_REQUIRED));
    }

    /**
     * 根据 StepId 获取指定步骤
     *
     * @param stepId 步骤ID
     * @return 指定步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public WorkflowStepWrapper getStepWrapper(String stepId) {
        return CollectionUtil.emptyIfNull(steps).stream()
                .filter(item -> (item.getName().equals(stepId) || item.getField().equals(stepId)))
                .findFirst()
                .orElseThrow(() -> ServiceExceptionUtil.exception(ErrorCodeConstants.EXECUTE_APP_STEP_NON_EXISTENT, stepId));
    }

    /**
     * 获取指定步骤之前的所有步骤，包括指定步骤
     *
     * @param stepId 步骤ID
     * @return 指定步骤之前的所有步骤
     */
    @JsonIgnore
    @JSONField(serialize = false)
    public List<WorkflowStepWrapper> getPreStepWrappers(String stepId) {
        List<WorkflowStepWrapper> preStepList = new ArrayList<>();
        for (WorkflowStepWrapper wrapper : steps) {
            if (!wrapper.getField().equals(stepId) || !wrapper.getName().equals(stepId)) {
                preStepList.add(wrapper);
            } else {
                preStepList.add(wrapper);
                break;
            }
        }
        return preStepList;
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
        for (WorkflowStepWrapper step : this.steps) {
            step.setActionResponse(stepId, response);
        }
    }

}
