package com.starcloud.ops.business.app.feign.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeResponse<T> implements java.io.Serializable {

    private static final long serialVersionUID = 3667983188357138391L;

    /**
     * 状态码。
     * 0 代表调用成功。
     */
    @JsonProperty("code")
    private Integer code;

    /**
     * 状态信息。API 调用失败时可通过此字段查看详细错误信息。
     */
    @JsonProperty("msg")
    private String msg;

    /**
     * 本次对话的基本信息。详细说明可参考 Chat Result。
     */
    @JsonProperty("data")
    private T data;

    @JsonProperty("debug_url")
    private String debugUrl;


}
