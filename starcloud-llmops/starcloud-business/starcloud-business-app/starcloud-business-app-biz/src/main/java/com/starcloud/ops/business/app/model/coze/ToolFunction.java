package com.starcloud.ops.business.app.model.coze;

import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class ToolFunction implements java.io.Serializable {

    private static final long serialVersionUID = 2066411103620670358L;

    /**
     * 方法名称
     */
    private String name;

    /**
     * 方法参数
     */
    private String argument;
}
