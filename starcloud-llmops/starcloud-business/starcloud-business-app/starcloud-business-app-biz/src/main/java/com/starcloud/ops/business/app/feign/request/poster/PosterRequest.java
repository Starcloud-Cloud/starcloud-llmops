package com.starcloud.ops.business.app.feign.request.poster;

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
@Schema(name = "PosterRequest", description = "海报请求")
public class PosterRequest implements java.io.Serializable {

    private static final long serialVersionUID = -3977808759871963692L;

    /**
     * 请求ID
     */
    @Schema(description = "请求ID")
    private String id;

    /**
     * 请求参数
     */
    @Schema(description = "请求参数")
    private Map<String, Object> params;
}
