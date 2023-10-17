package com.starcloud.ops.business.app.domain.entity.workflow;

import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 工作流步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class WorkflowStepEntity extends ActionEntity {

    /**
     * 步骤版本，默认版本 1
     */
    private Integer version;

    /**
     * 步骤图标
     */
    private String icon;

    /**
     * 步骤标签
     */
    private List<String> tags;

    /**
     * 步骤场景
     */
    private List<String> scenes;

    /**
     * 模版变量
     */
    private VariableEntity variable;

    /**
     * 是否自动执行
     */
    private Boolean isAuto;

    /**
     * 是否是可编辑步骤
     */
    private Boolean isCanEditStep;

}
