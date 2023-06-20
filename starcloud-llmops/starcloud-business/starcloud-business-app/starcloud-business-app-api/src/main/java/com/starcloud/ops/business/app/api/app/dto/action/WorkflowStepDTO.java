package com.starcloud.ops.business.app.api.app.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.dto.variable.VariableDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * AIGC 步骤 DTO，不同的步骤，会有不同的配置。
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-19
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用步骤对象")
public class WorkflowStepDTO extends ActionDTO {

    private static final long serialVersionUID = 157666534536774534L;

    /**
     * 是否自动执行
     */
    @Schema(description = "是否自动执行")
    private Boolean isAuto;

    /**
     * 是否是可编辑步骤
     */
    @Schema(description = "是否是可编辑步骤")
    private Boolean isCanEditStep;

    /**
     * 步骤版本，默认版本 1.0.0
     */
    @Schema(description = "步骤版本，默认版本 1")
    private Integer version;

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
    private VariableDTO variable;

    /**
     * 步骤图标
     */
    @Schema(description = "步骤图标")
    private String icon;


}
