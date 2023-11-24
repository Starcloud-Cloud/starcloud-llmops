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
@Schema(name = "PosterResponse", description = "海报请求响应")
public class PosterDTO implements java.io.Serializable {

    private static final long serialVersionUID = -1181017995368474414L;

    /**
     * 海报地址
     */
    @Schema(description = "海报地址")
    private String url;

}
