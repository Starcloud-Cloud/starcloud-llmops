package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeLastError implements java.io.Serializable {

    private static final long serialVersionUID = -6134565580188557993L;

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
