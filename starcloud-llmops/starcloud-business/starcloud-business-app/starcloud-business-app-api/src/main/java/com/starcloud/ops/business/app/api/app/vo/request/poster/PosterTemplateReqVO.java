package com.starcloud.ops.business.app.api.app.vo.request.poster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.request.variable.VariableItemReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Valid
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "海报风格对象 VO")
public class PosterTemplateReqVO implements Serializable {

    private static final long serialVersionUID = -7900681525886053825L;

    /**
     * 海报模板ID
     */
    @Schema(description = "海报模板ID")
    @NotBlank(message = "海报模板ID不能为空")
    private String id;

    /**
     * 海报模板名称
     */
    @Schema(description = "海报模板名称")
    private String name;

    /**
     * 海报模板描述
     */
    @Schema(description = "海报模板描述")
    private String description;

    /**
     * 海报模板封面
     */
    @Schema(description = "海报模板封面")
    private String example;

    /**
     * 海报模板变量
     */
    @Schema(description = "海报模板变量")
    @Valid
    private List<VariableItemReqVO> variables;

}
