package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeRequiredAction {

    /**
     * 类型
     */
    @JsonProperty("type")
    private String type;

    /**
     * 提交工具输出
     */
    @JsonProperty("submit_tool_outputs")
    private CozeOutputTool submitToolOutputs;


}
