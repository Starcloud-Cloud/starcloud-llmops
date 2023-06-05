package com.starcloud.ops.business.app.api.template.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * AIGC 步骤包装类 DTO，不同的步骤，会有不同的配置。
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "步骤包装类")
public class StepWrapperDTO implements Serializable {

    private static final long serialVersionUID = 1578678534536774534L;

    /**
     * 步骤label
     */
    @Schema(description = "步骤label")
    private String name;

    /**
     * 步骤 field
     */
    @Schema(description = "步骤 field")
    private String field;

    /**
     * 步骤按钮label
     */
    @Schema(description = "步骤按钮label")
    private String buttonLabel;

    /**
     * 具体的步骤配置
     */
    @Schema(description = "具体的步骤配置")
    private StepDTO step;

    /**
     * 步骤变量,执行
     */
    @Schema(description = "步骤变量")
    private List<VariableDTO> variables;

    /**
     * 步骤描述
     */
    @Schema(description = "步骤描述")
    private String description;

}
