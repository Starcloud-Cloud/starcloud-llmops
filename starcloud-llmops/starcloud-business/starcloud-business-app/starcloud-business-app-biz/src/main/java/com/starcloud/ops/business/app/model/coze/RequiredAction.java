package com.starcloud.ops.business.app.model.coze;

import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class RequiredAction {

    /**
     * 类型
     */
    private String type;

    /**
     * 提交工具输出
     */
    private OutputTool submitToolOutputs;

}
