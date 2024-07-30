package com.starcloud.ops.business.app.model.coze;

import lombok.Data;

/**
 * Token 消耗的详细信息。实际的 Token 消耗以对话结束后返回的值为准。
 *
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class Usage implements java.io.Serializable {

    private static final long serialVersionUID = 1862741645841305856L;

    /**
     * 本次对话消耗的 Token 总数，包括 input 和 output 部分的消耗。
     */
    private Integer tokenCount;

    /**
     * output 部分消耗的 Token 总数。
     */
    private Integer outputCount;

    /**
     * input 部分消耗的 Token 总数。
     */
    private Integer inputCount;
}
