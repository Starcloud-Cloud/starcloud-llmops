package com.starcloud.ops.business.app.model.content;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.starcloud.ops.business.app.feign.dto.PosterImageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(description = "图片内容对象")
public class ImageContent implements java.io.Serializable {

    private static final long serialVersionUID = -3258707784175083990L;

    /**
     * 图片模板code
     */
    @Schema(description = "图片模板code")
    @JsonPropertyDescription("图片模板编码")
    private String code;

    /**
     * 海报图片模板名称
     */
    @Schema(description = "图片模板名称")
    @JsonPropertyDescription("图片模板名称")
    private String name;

    /**
     * 是否是海报主图
     */
    @Schema(description = "是否是主图")
    @JsonPropertyDescription("是否是主图")
    private Boolean isMain;

    /**
     * 海报图片序号
     */
    @Schema(description = "图片序号")
    @JsonPropertyDescription("图片序号")
    private Integer index;

    /**
     * 海报图片地址
     */
    @Schema(description = "海报图片地址")
    @JsonPropertyDescription("图片地址")
    private String url;

    /**
     * 海报参数
     */
    @Schema(description = "海报参数")
    @JsonPropertyDescription("图片参数")
    private Map<String, PosterImageParam> finalParams;
}
