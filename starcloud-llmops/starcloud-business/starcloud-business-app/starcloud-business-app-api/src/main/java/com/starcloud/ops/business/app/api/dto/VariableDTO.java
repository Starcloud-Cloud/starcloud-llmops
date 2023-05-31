package com.starcloud.ops.business.app.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.framework.common.api.dto.Option;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 变量 DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "变量实体")
public class VariableDTO implements Serializable {

    private static final long serialVersionUID = 1345678534536774534L;

    /**
     * 变量 label
     */
    @Schema(description = "变量 label")
    private String label;

    /**
     * 变量 field
     */
    @Schema(description = "变量 field")
    private String field;

    /**
     * 变量默认值
     */
    @Schema(description = "变量默认值")
    private Object defaultValue;

    /**
     * 变量值
     */
    @Schema(description = "变量值")
    private Object value;

    /**
     * 变量类型
     */
    @Schema(description = "变量类型")
    private String type;

    /**
     * 变量选项, 变量类型为 SELECT 时使用
     */
    @Schema(description = "变量选项")
    private List<Option> options;

    /**
     * 变量分组
     */
    @Schema(description = "变量分组")
    private String group;

    /**
     * 变量样式
     */
    @Schema(description = "变量样式")
    private String style;

    /**
     * 变量是否显示
     */
    @Schema(description = "变量是否显示")
    private Boolean isShow;

    /**
     * 变量是否为点位
     */
    @Schema(description = "变量是否为点位")
    private Boolean isPoint;

    /**
     * 变量排序
     */
    @Schema(description = "变量排序")
    private Integer order;

    /**
     * 模版描述
     */
    @Schema(description = "模版描述")
    private String description;

}
