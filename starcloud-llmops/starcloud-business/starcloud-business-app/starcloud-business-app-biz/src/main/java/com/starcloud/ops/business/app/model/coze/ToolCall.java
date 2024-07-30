package com.starcloud.ops.business.app.model.coze;

import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class ToolCall {

    /**
     * 工具 ID。
     */
    private String id;

    /**
     * 工具类型
     */
    private String type;

    /**
     * 工具名称
     */
    private ToolFunction function;


}
