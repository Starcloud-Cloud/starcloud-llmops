package com.starcloud.ops.business.app.model.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class LastError implements java.io.Serializable {

    private static final long serialVersionUID = 4328352372172483364L;

    /**
     * 错误码。
     */
    @JsonProperty("code")
    private Integer code;

    /**
     * 错误信息。
     */
    @JsonProperty("msg")
    private String msg;
}
