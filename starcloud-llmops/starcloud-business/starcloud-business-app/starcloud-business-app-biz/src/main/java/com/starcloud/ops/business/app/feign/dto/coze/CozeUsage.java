package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeUsage implements java.io.Serializable {

    private static final long serialVersionUID = 3997266707907314647L;
    
    /**
     * 本次对话消耗的 Token 总数，包括 input 和 output 部分的消耗。
     */
    @JsonProperty("token_count")
    private Integer tokenCount;

    /**
     * output 部分消耗的 Token 总数。
     */
    @JsonProperty("output_count")
    private Integer outputCount;

    /**
     * input 部分消耗的 Token 总数。
     */
    @JsonProperty("input_count")
    private Integer inputCount;
}
