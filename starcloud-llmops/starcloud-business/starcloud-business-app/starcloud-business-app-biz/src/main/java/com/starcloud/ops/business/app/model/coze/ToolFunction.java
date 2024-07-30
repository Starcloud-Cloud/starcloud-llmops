package com.starcloud.ops.business.app.model.coze;

import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class ToolFunction {

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具参数
     */
    private String argument;
}
