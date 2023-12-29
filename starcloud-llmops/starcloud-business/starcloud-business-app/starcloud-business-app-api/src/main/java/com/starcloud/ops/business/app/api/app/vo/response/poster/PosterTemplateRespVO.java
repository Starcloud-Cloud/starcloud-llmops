package com.starcloud.ops.business.app.api.app.vo.response.poster;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.app.vo.response.variable.VariableItemRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "海报风格对象 VO")
public class PosterTemplateRespVO implements Serializable {

    private static final long serialVersionUID = -7900681525886053825L;

    /**
     * 海报模板ID
     */
    @Schema(description = "海报模板ID")
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
    private List<VariableItemRespVO> variables;

}
