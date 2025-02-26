package com.starcloud.ops.business.app.model.creative;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * JSON Schema 选项
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "JSON Schema 选项")
public class CreativeOptionDTO implements Serializable {

    private static final long serialVersionUID = -1216916847413834126L;

    /**
     * 选项的父编码
     */
    @Schema(description = "父属性编码")
    private String parentCode;

    /**
     * 选项的编码
     */
    @Schema(description = "属性编码")
    private String code;

    /**
     * 选项的名称
     */
    @Schema(description = "属性名称")
    private String name;

    /**
     * 选项的类型
     */
    @Schema(description = "属性数据类型类型")
    private String type;

    /**
     * 选项的模型
     */
    @Schema(description = "属性模型类型")
    private String model;

    /**
     * 选项的描述
     */
    @Schema(description = "描述，用于展示，对该选项的描述")
    private String description;

    @Schema(description = "步骤编码")
    private String stepHandler;

    /**
     * 子选项
     */
    @Schema(description = "子选项")
    @JsonPropertyDescription("子选项")
    private List<CreativeOptionDTO> children;


    /**
     * 节点入参
     */
    @Schema(description = "节点入参")
    private String inJsonSchema;

    /**
     * 节点出参
     */
    @Schema(description = "节点出参")
    private String outJsonSchema;

    @Schema(description = "是否为当前节点")
    private boolean currentStep;


}