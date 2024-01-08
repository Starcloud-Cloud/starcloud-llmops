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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "code")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ContentSchemeStepDTO.class, name = "ContentActionHandler"),
        @JsonSubTypes.Type(value = ParagraphSchemeStepDTO.class, name = "ParagraphActionHandler"),
        @JsonSubTypes.Type(value = AssembleSchemeStepDTO.class, name = "AssembleActionHandler"),
        @JsonSubTypes.Type(value = PosterSchemeStepDTO.class, name = "PosterActionHandler")
})
public abstract class BaseSchemeStepDTO implements java.io.Serializable {

    private static final long serialVersionUID = 5401242096922842719L;

    /**
     * 唯一
     */
    private String code;

    /**
     * 名称
     */
    private String name;

}
