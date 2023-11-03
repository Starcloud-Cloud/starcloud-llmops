package com.starcloud.ops.business.app.api.xhs;

import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
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
@Schema(name = "XhsImageTemplateDTO", description = "小红书图片模板")
public class XhsImageTemplateDTO implements java.io.Serializable {

    private static final long serialVersionUID = 20366484377479364L;

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
     * 图片模板变量
     */
    @Schema(description = "图片模板变量")
    private List<VariableItemRespVO> variables;

}
