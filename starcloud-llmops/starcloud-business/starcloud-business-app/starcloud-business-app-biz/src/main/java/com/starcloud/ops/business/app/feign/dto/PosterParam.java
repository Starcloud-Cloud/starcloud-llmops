package com.starcloud.ops.business.app.feign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "PosterParam", description = "海报请求参数")
public class PosterParam implements java.io.Serializable {

    private static final long serialVersionUID = 8721435087546065535L;

    /**
     * 参数id
     */
    @Schema(description = "参数id")
    private String id;

    /**
     * 参数名称
     */
    @Schema(description = "参数名称")
    private String name;

    /**
     * 参数值
     */
    @Schema(description = "排序")
    private Integer order;

    /**
     * 参数值
     */
    @Schema(description = "参数类型")
    private String type;

}
