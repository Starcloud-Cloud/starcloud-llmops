package com.starcloud.ops.business.app.api.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AIGC 步骤 DTO，不同的步骤，会有不同的配置。
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用步骤对象")
public class StepDTO implements Serializable {

    private static final long serialVersionUID = 157666534536774534L;

    /**
     * 步骤名称
     */
    @Schema(description = "步骤名称")
    private String name;

    /**
     * 步骤类型
     */
    @Schema(description = "步骤类型")
    private String type;

    /**
     * 是否自动执行
     */
    @Schema(description = "是否自动执行")
    private Boolean isAuto;

    /**
     * 步骤版本，默认版本 1.0.0
     */
    @Schema(description = "步骤版本，默认版本 1.0.0")
    private String version;

    /**
     * 步骤标签
     */
    @Schema(description = "步骤标签")
    private List<String> tags;

    /**
     * 步骤场景
     */
    @Schema(description = "步骤场景")
    private List<String> scenes;

    /**
     * 步骤变量
     */
    @Schema(description = "步骤变量")
    private List<VariableDTO> variables;

    /**
     * 步骤执行结果
     */
    @Schema(description = "步骤执行结果")
    private StepResponse response;

    /**
     * 步骤图标
     */
    @Schema(description = "步骤图标")
    private String icon;

    /**
     * 步骤描述
     */
    @Schema(description = "步骤描述")
    private String description;


}
