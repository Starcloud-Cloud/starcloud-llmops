package com.starcloud.ops.business.app.model.coze;

import lombok.Data;

import java.util.List;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class OutputTool implements java.io.Serializable {

    private static final long serialVersionUID = 8989164680434466844L;

    /**
     * 具体上报信息详情。
     */
    private List<ToolCall> toolCalls;
}
