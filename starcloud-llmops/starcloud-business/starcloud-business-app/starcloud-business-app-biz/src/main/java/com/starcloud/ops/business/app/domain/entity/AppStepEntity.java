package com.starcloud.ops.business.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 基础步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppStepEntity implements Serializable {

    private static final long serialVersionUID = 9024501399144126063L;

    /**
     * 步骤名称
     */
    private String name;

    /**
     * 步骤类型
     */
    private String type;

    /**
     * 步骤处理器
     */
    private String handler;

    /**
     * 是否自动执行
     */
    private Boolean isAuto;

    /**
     * 步骤版本，默认版本 1
     */
    private Integer version;

    /**
     * 步骤标签
     */
    private List<String> tags;

    /**
     * 步骤场景
     */
    private List<String> scenes;

    /**
     * 步骤变量
     */
    private List<AppVariableEntity> variables;

    /**
     * 步骤执行结果
     */
    private AppStepResponse response;

    /**
     * 步骤图标
     */
    private String icon;

    /**
     * 步骤描述
     */
    private String description;


}
