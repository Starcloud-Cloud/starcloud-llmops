package com.starcloud.ops.business.app.api.app.vo.response.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 工作流步骤实体
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-05-31
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用 action 工作流步骤响应对象 VO")
public class WorkflowStepRespVO extends ActionRespVO {

    private static final long serialVersionUID = 1370359151602184535L;

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


    public void supplementFlowStep(WorkflowStepRespVO flowStep) {

    }
}
