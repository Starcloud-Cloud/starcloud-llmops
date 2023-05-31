package com.starcloud.ops.business.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * App 步骤实体包装类
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppStepWrapper {

    /**
     * 步骤label
     */
    private String name;

    /**
     * 步骤 field
     */
    private String field;

    /**
     * 步骤按钮label
     */
    private String buttonLabel;

    /**
     * 具体的步骤配置
     */
    private AppStepEntity step;

    /**
     * 步骤变量,执行
     */
    private List<AppVariableEntity> variables;

    /**
     * 步骤描述
     */
    private String description;
}
