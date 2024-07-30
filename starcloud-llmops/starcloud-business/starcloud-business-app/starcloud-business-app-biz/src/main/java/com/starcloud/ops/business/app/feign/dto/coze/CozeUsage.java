package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeUsage {

    @JsonProperty("token_count")
    private Integer tokenCount;

    @JsonProperty("output_count")
    private Integer outputCount;

    @JsonProperty("input_count")
    private Integer inputCount;
}
