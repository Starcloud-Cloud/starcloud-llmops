package com.starcloud.ops.business.app.api.xhs.scheme.dto.config.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "BaseSchemeStepDTO", description = "方案步骤")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "code", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TitleSchemeStepDTO.class, name = "TitleActionHandler"),
        @JsonSubTypes.Type(value = CustomSchemeStepDTO.class, name = "CustomActionHandler"),
        @JsonSubTypes.Type(value = ParagraphSchemeStepDTO.class, name = "ParagraphActionHandler"),
        @JsonSubTypes.Type(value = AssembleSchemeStepDTO.class, name = "AssembleActionHandler"),
        @JsonSubTypes.Type(value = PosterSchemeStepDTO.class, name = "PosterActionHandler"),
        @JsonSubTypes.Type(value = VariableSchemeStepDTO.class, name = "VariableActionHandler")
})
public abstract class BaseSchemeStepDTO implements java.io.Serializable {

    private static final long serialVersionUID = 5401242096922842719L;

    /**
     * 对应应用 step 的 handler
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 校验
     */
    public abstract void validate();

}
