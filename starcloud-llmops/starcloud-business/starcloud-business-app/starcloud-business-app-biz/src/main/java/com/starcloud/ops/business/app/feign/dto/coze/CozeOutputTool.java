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
public class CozeOutputTool {

    /**
     * 会话 ID，即会话的唯一标识。
     */
    @JsonProperty("tool_calls")
    private List<CozeToolCall> toolCalls;
}
