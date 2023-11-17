package com.starcloud.ops.business.app.api.scheme.dto;

import com.starcloud.ops.business.app.api.xhs.XhsImageTemplateDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "图片示例")
public class ImageExampleDTO implements java.io.Serializable {

    private static final long serialVersionUID = 403097486443239911L;

    /**
     * 风格id
     */
    @Schema(description = "风格id")
    private String id;

    /**
     * 风格名称
     */
    @Schema(description = "风格名称")
    private String name;

    /**
     * 模板列表
     */
    @Schema(description = "模板列表")
    private List<XhsImageTemplateDTO> templateList;

}
