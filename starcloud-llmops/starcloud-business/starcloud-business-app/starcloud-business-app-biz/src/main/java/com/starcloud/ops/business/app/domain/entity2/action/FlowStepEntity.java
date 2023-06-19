package com.starcloud.ops.business.app.domain.entity2.action;

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
public class FlowStepEntity extends ActionEntity {


    /**
     * 模版变量
     */
    private VariableEntity variable;


    /**
     * 步骤图标
     */
    private String icon;

    /**
     * 步骤版本，默认版本 1
     */
    private Integer version;

    /**
     * 是否自动执行
     */
    private Boolean isAuto;


    /**
     * 步骤标签
     */
    private List<String> tags;

    /**
     * 步骤场景
     */
    private List<String> scenes;


    /**
     * 步骤类型
     */
    private String type;

    /**
     * 步骤处理器
     */
    private String handler;


}
