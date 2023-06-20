package com.starcloud.ops.business.app.api.app.dto.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.action.WorkflowStepDTO;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableDTO;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import com.starcloud.ops.business.app.api.app.vo.response.action.WorkflowStepRespVO;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "步骤包装类")
public class WorkStepStepWrapperDTO implements Serializable {

    private static final long serialVersionUID = 1578678534536774534L;

    /**
     * 步骤 field
     */
    @Schema(description = "步骤 field")
    private String field;

    /**
     * 步骤label
     */
    @Schema(description = "步骤label")
    private String name;

    /**
     * 步骤按钮label
     */
    @Schema(description = "步骤按钮label")
    private String buttonLabel;

    /**
     * 步骤描述
     */
    @Schema(description = "步骤描述")
    private String description;

    /**
     * 具体的步骤配置
     */
    @Schema(description = "具体的步骤配置")
    private WorkflowStepDTO flowStep;

    /**
     * 步骤变量
     */
    @Schema(description = "步骤变量")
    private VariableDTO variable;

}
