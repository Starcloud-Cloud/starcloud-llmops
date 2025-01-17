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
@Schema(name = "PosterImageParam", description = "海报请求参数")
public class PosterImageParam implements java.io.Serializable {

    private static final long serialVersionUID = 2908405389322954426L;

    /**
     * 参数ID
     */
    @Schema(description = "参数ID")
    private String id;

    /**
     * 参数类型
     */
    @Schema(description = "参数类型")
    private String type;

    /**
     * 参数值
     */
    @Schema(description = "参数值")
    private String text;

}
