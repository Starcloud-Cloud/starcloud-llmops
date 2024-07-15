package com.starcloud.ops.business.app.model.poster;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CreativeImageTemplateTypeDTO", description = "创作中心图片模板类型对象")
public class PosterTemplateTypeDTO implements java.io.Serializable {

    private static final long serialVersionUID = 20366484377479364L;

    /**
     * 图片模板类型ID
     */
    @Schema(description = "图片模板类型ID")
    private String id;

    /**
     * 图片模板类型名称
     */
    @Schema(description = "图片模板类型名称")
    private String name;

    /**
     * 图片模板类型排序
     */
    @Schema(description = "图片模板类型排序")
    private Integer order;

    /**
     * 海报模板类型下的海报模板
     */
    @Schema(description = "海报模板类型下的海报模板")
    private List<PosterTemplateDTO> list;
}
