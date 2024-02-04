package com.starcloud.ops.business.app.service.xhs.scheme.entity.step;

import com.starcloud.ops.business.app.api.app.vo.response.config.WorkflowStepWrapperRespVO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public abstract class BaseSchemeStepEntity implements java.io.Serializable {

    private static final long serialVersionUID = 5401242096922842719L;

    /**
     * 对应应用 step 的 handler
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 组装为应用步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    protected abstract void doTransformAppStep(WorkflowStepWrapperRespVO stepWrapper);

    /**
     * 组装为方案步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    protected abstract void doTransformSchemeStep(WorkflowStepWrapperRespVO stepWrapper);

    /**
     * 组装为应用步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    public void transformAppStep(WorkflowStepWrapperRespVO stepWrapper) {
        this.doTransformAppStep(stepWrapper);
    }

    /**
     * 组装为方案步骤信息
     *
     * @param stepWrapper 应用步骤
     */
    public void transformSchemeStep(WorkflowStepWrapperRespVO stepWrapper) {
        this.code = stepWrapper.getFlowStep().getHandler();
        this.name = stepWrapper.getName();
        this.doTransformSchemeStep(stepWrapper);
    }

}
