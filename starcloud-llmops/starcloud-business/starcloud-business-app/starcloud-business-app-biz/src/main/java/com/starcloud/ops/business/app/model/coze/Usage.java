package com.starcloud.ops.business.app.model.coze;

import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class Usage {

    /**
     * 总token消耗数。
     */
    private Integer tokenCount;

    /**
     * 总输出数。
     */
    private Integer outputCount;

    /**
     * 总输入数。
     */
    private Integer inputCount;
}
