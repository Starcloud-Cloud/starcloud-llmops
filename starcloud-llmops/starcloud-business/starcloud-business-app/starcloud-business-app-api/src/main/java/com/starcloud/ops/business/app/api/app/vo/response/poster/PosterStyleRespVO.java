package com.starcloud.ops.business.app.api.app.vo.response.poster;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class PosterStyleRespVO implements Serializable {

    private static final long serialVersionUID = 1093411003287488657L;

    /**
     * 海报风格ID
     */
    @Schema(description = "海报风格ID")
    private String id;

    /**
     * 海报风格名称
     */
    @Schema(description = "海报风格名称")
    private String name;

    /**
     * 海报风格描述
     */
    @Schema(description = "海报风格描述")
    private String description;

    /**
     * 海报模板列表
     */
    @Schema(description = "海报模板列表")
    private List<PosterTemplateRespVO> templateList;
}
