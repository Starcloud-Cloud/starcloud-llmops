package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeFunction {

    /**
     * 工具名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 工具参数
     */
    @JsonProperty("argument")
    private String argument;
}
