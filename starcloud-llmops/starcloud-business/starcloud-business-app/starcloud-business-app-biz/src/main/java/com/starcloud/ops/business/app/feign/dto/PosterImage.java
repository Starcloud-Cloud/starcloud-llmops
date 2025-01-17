package com.starcloud.ops.business.app.feign.dto;

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
@Schema(description = "海报请求响应")
public class PosterImage implements java.io.Serializable {

    private static final long serialVersionUID = -1181017995368474414L;

    /**
     * 海报地址
     */
    @Schema(description = "海报地址")
    private String url;

    /**
     * 海报参数
     */
    @Schema(description = "海报参数")
    private Map<String, PosterImageParam> finalParams;

}
