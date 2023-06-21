package com.starcloud.ops.business.app.domain.entity.config;

import com.alibaba.fastjson.annotation.JSONField;
import com.starcloud.ops.business.app.domain.entity.action.WorkflowStepEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableEntity;
import com.starcloud.ops.business.app.domain.entity.variable.VariableItemEntity;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * App 步骤实体包装类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
public class WorkflowStepWrapper {

    /**
     * 步骤 field
     */
    private String field;


    /**
     * 步骤label
     */
    private String name;


    /**
     * 步骤按钮label
     */
    private String buttonLabel;


    /**
     * 步骤描述
     */
    private String description;


    /**
     * 具体的步骤配置
     */
    private WorkflowStepEntity flowStep;

    /**
     * 步骤变量,执行
     */
    private VariableEntity variable;


    /**
     * 获取指定类型变量
     *
     * @param type
     * @return
     */
    @JSONField(serialize = false)
    public List<VariableItemEntity> getVariable(String type) {
        return null;
    }


    /**
     * 获取当前步骤的变量Key列表
     *
     * @return
     */
    @JSONField(serialize = false)
    public Map<String, String> getContextVariablesKeys() {


        return new HashMap() {{
            put("_STEP.xxx._OUT", "hah");
        }};
    }

    /**
     * 获取当前步骤的所有变量的values
     *
     * @return
     */
    @JSONField(serialize = false)
    public Map<String, Object> getContextVariablesMaps() {

        return new HashMap() {{
            put("test", 1);
            put("test32", 45);
        }};
    }

    /**
     * 获取当前步骤的变量Key列表
     *
     * @return
     */
    @JSONField(serialize = false)
    public <T> T getContextVariablesValue(String key) {

        return null;
    }

    /**
     * 获取当前步骤的变量Key列表
     *
     * @return
     */
    @JSONField(serialize = false)
    public <T> T getContextVariablesValue(String key, T def) {

        return def;
    }


}
