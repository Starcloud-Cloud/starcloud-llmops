package com.starcloud.ops.business.app.api.xhs.scheme.dto;

import com.starcloud.ops.business.app.api.xhs.XhsImageStyleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

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
@Schema(name = "CreativeSchemeImageTemplateDTO", description = "图片生成模板")
public class CreativeSchemeImageTemplateDTO implements java.io.Serializable {

    private static final long serialVersionUID = 1330559953484705125L;

    /**
     * 图片生成模板：图片风格
     */
    @Valid
    @NotEmpty(message = "图片生成模板：图片风格不能为空")
    @Schema(description = "图片生成风格List")
    private List<XhsImageStyleDTO> styleList;

}
