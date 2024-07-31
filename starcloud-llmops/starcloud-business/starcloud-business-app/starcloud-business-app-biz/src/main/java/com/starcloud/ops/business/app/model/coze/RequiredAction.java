package com.starcloud.ops.business.app.model.coze;

import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class RequiredAction implements java.io.Serializable {

    private static final long serialVersionUID = 4093758970720841595L;

    /**
     * 额外操作的类型，枚举值为 submit_tool_outputs。
     */
    private String type;

    /**
     * 需要提交的结果详情，通过提交接口上传，并可以继续聊天
     */
    private OutputTool submitToolOutputs;

}
