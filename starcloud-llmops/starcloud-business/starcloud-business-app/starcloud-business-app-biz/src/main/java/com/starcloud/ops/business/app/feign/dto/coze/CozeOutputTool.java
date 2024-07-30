package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeOutputTool implements java.io.Serializable {

    private static final long serialVersionUID = -8848016117885313778L;

    /**
     * 具体上报信息详情。
     */
    @JsonProperty("tool_calls")
    private List<CozeToolCall> toolCalls;
}
