package com.starcloud.ops.business.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.dto.VariableDTO;
import lombok.Data;

import java.util.List;

/**
 * 基础步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppStepEntity {

    /**
     * 步骤名称
     */
    private String name;

    /**
     * 步骤类型
     */
    private String type;

    /**
     * 是否自动执行
     */
    private Boolean isAuto;

    /**
     * 步骤版本，默认版本 1.0.0
     */
    private String version;

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
    private List<VariableDTO> variables;

    /**
     * 步骤执行结果
     */
    private String response;

    /**
     * 步骤图标
     */
    private String icon;

    /**
     * 步骤描述
     */
    private String description;


}
