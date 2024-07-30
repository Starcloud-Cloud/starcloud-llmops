package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeToolCall {

    /**
     * 工具 ID。
     */
    @JsonProperty("id")
    private String id;

    /**
     * 工具类型
     */
    @JsonProperty("type")
    private String type;

    /**
     * 工具名称
     */
    @JsonProperty("function")
    private CozeFunction function;


}
