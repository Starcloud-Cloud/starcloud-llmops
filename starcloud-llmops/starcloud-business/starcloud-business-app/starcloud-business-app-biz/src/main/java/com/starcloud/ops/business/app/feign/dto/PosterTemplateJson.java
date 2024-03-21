package com.starcloud.ops.business.app.feign.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2021-06-22
 */
@Data
public class PosterTemplateJson {

    /**
     * id
     */
    @Schema(description = "id")
    private String id;

    /**
     * label
     *
     */
    @Schema(description = "label")
    private String label;

    /**
     * json
     */
    @Schema(description = "json")
    private String json;
}
