package com.starcloud.ops.business.app.domain.entity.poster;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
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
public class PosterStyleEntity implements Serializable {

    private static final long serialVersionUID = 1093411003287488657L;

    /**
     * 海报风格ID
     */
    @Schema(description = "海报风格ID")
    @NotBlank(message = "海报风格ID不能为空")
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
    @NotEmpty(message = "海报模板列表不能为空")
    @Valid
    private List<PosterTemplateEntity> templateList;
}
