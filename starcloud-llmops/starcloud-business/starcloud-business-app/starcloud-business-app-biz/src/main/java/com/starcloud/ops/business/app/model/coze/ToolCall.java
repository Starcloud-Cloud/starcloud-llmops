package com.starcloud.ops.business.app.model.coze;

import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class ToolCall implements java.io.Serializable {

    private static final long serialVersionUID = -3719080105771430879L;

    /**
     * 上报运行结果的 ID。
     */
    private String id;

    /**
     * 工具类型，枚举值为 function。
     */
    private String type;

    /**
     * 执行方法 function 的定义。
     */
    private ToolFunction function;


}
