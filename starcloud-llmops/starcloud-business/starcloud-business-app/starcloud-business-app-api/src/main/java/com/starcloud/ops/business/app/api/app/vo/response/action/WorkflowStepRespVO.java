package com.starcloud.ops.business.app.api.app.vo.response.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.params.JsonDataVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;
import java.util.Optional;

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
        if (Objects.isNull(flowStep)) {
            return;
        }
        if ("AssembleActionHandler".equals(getHandler())) {
            ActionResponseRespVO response = flowStep.getResponse();
            JsonDataVO output = Optional.ofNullable(response.getOutput()).orElse(new JsonDataVO());
            // 固定 jsonSchema,因为现在数据库中的值是固定的。更新，保证数据库中的值，均有描述信息。
            // todo 等到此处返回结果的jsonschema 可以自定义的时候，此处需要进行重新处理。
            output.setJsonSchema("{\"type\":\"object\",\"id\":\"urn:jsonschema:com:starcloud:ops:business:app:model:content:CopyWritingContent\",\"properties\":{\"title\":{\"type\":\"string\",\"description\":\"标题\"},\"content\":{\"type\":\"string\",\"description\":\"内容\"},\"tagList\":{\"type\":\"array\",\"description\":\"标签\",\"items\":{\"type\":\"string\"}}}}");
            response.setOutput(output);
            flowStep.setResponse(response);
        }
        if ("CustomActionHandler".equals(getHandler())) {
            ActionResponseRespVO response = flowStep.getResponse();
            JsonDataVO output = Optional.ofNullable(response.getOutput()).orElse(new JsonDataVO());
            // 固定 jsonSchema,因为现在数据库中的值是固定的。更新，保证数据库中的值，均有描述信息。
            // todo 等到此处返回结果的jsonschema 可以自定义的时候，此处需要进行重新处理。
            output.setJsonSchema(null);
            response.setOutput(output);
            flowStep.setResponse(response);
        }
        if (Objects.nonNull(this.getVariable())) {
            this.getVariable().supplementStepVariable(flowStep.getVariable());
        }
    }

    public void merge(WorkflowStepRespVO flowStep) {
        if (Objects.isNull(flowStep) || Objects.isNull(flowStep.getVariable())) {
            return;
        }
        this.getVariable().merge(flowStep.getVariable());
    }
}
