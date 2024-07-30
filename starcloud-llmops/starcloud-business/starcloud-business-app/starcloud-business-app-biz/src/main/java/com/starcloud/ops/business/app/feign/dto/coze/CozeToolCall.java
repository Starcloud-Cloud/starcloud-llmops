package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeToolCall implements java.io.Serializable {

    private static final long serialVersionUID = 3212035416343471748L;

    /**
     * 上报运行结果的 ID。
     */
    @JsonProperty("id")
    private String id;

    /**
     * 工具类型，枚举值为 function。
     */
    @JsonProperty("type")
    private String type;

    /**
     * 执行方法 function 的定义。
     */
    @JsonProperty("function")
    private CozeFunction function;


}
