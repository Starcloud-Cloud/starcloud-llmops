package com.starcloud.ops.business.app.domain.entity2.config;

import com.starcloud.ops.business.app.domain.entity.AppVariableEntity;
import com.starcloud.ops.business.app.domain.entity2.action.FlowStepEntity;
import com.starcloud.ops.business.app.domain.entity2.variable.VariableEntity;
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
public class FlowStepWrapper {

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
    private FlowStepEntity flowStep;

    /**
     * 步骤变量,执行
     */
    private VariableEntity variables;


    /**
     * 获取指定类型变量
     *
     * @param type
     * @return
     */
    public List<AppVariableEntity> getVariables(String type) {
        return null;
    }


    /**
     * 获取当前步骤的变量Key列表
     *
     * @return
     */
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
    public <T> T getContextVariablesValue(String key) {

        return null;
    }

    /**
     * 获取当前步骤的变量Key列表
     *
     * @return
     */
    public <T> T getContextVariablesValue(String key, T def) {

        return def;
    }


}
