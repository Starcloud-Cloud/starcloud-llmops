package com.starcloud.ops.business.app.api.xhs;

import com.starcloud.ops.business.app.api.app.dto.variable.VariableItemDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-02
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "XhsImageTemplateResponse", description = "小红书图片模板响应")
public class XhsImageTemplateResponse implements java.io.Serializable {

    private static final long serialVersionUID = -8706726775214601674L;

    /**
     * 图片模板ID
     */
    @Schema(description = "图片模板ID")
    private String id;

    /**
     * 图片模板名称
     */
    @Schema(description = "图片模板名称")
    private String name;

    /**
     * 图片数量
     */
    @Schema(description = "图片数量")
    private Integer imageNumber;

    /**
     * 示例图片
     */
    @Schema(description = "示例图片")
    private String example;

    /**
     * 图片模板变量
     */
    @Schema(description = "图片模板变量")
    private List<VariableItemDTO> variables;

}
