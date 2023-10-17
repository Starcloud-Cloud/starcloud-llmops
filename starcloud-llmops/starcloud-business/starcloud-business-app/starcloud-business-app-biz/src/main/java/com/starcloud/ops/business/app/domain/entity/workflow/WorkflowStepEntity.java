package com.starcloud.ops.business.app.domain.entity.workflow;

import cn.hutool.core.collection.CollectionUtil;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    /**
     * 设置模型变量
     *
     * @param key   key
     * @param value value
     */
    public void setModelVariable(String key, Object value) {
        List<VariableItemEntity> variables = Optional.ofNullable(this.variable).map(VariableEntity::getVariables).orElse(new ArrayList<>());
        if (CollectionUtil.isEmpty(variables)) {
            return;
        }
        for (VariableItemEntity variableItem : variables) {
            if (variableItem.getField().equalsIgnoreCase(key)) {
                variableItem.setValue(value);
            }
        }
    }
}
