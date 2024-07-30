package com.starcloud.ops.business.app.model.coze;

import lombok.Data;

import java.util.List;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class OutputTool {

    /**
     * 会话 ID，即会话的唯一标识。
     */
    private List<ToolCall> toolCalls;
}
