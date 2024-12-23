package com.starcloud.ops.business.app.api.app.dto.variable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.framework.common.api.dto.Option;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 变量DTO
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-19
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "变量实体DTO")
public class VariableItemDTO implements Serializable {

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
     * 变量类型
     */
    @Schema(description = "变量类型")
    private String type;

    /**
     * 变量样式
     */
    @Schema(description = "变量样式")
    private String style;

    /**
     * 变量分组
     */
    @Schema(description = "变量分组")
    private String group;

    /**
     * 变量排序
     */
    @Schema(description = "变量排序")
    private Integer order;

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
     * 是否升级<br>
     * 如果该值为 true, 则保留用户配置的值，否则则使用系统默认值。
     */
    private Boolean isKeepUserValue;

    /**
     * 应用描述
     */
    @Schema(description = "应用描述")
    private String description;

    /**
     * 变量选项, 变量类型为 SELECT 时使用
     */
    @Schema(description = "变量选项")
    private List<Option> options;

    /**
     * 字符数量
     */
    @Schema(description = "字符数量")
    private Integer count;

}
