package com.starcloud.ops.business.app.api.scheme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-14
 */
@Valid
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "CreativeSchemeConfigDTO", description = "创作方案配置")
public class CreativeSchemeConfigDTO implements java.io.Serializable {

    private static final long serialVersionUID = 8377338212121361722L;

    /**
     * 文案生成模板
     */
    @Valid
    @NotNull(message = "文案生成模板不能为空！")
    @Schema(description = "文案生成模板")
    private CreativeSchemeCopyWritingTemplateDTO copyWritingTemplate;

    /**
     * 图片生成模板：图片风格
     */
    @Valid
    @NotNull(message = "图片生成模板：图片模板不能为空")
    @Schema(description = "图片模板")
    private CreativeSchemeImageTemplateDTO imageTemplate;

}