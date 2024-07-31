package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author nacoyer
 * @date 2021-06-22
 * @since 1.0.0
 */
@Data
public class CozeRequiredAction implements java.io.Serializable {

    private static final long serialVersionUID = -4904459416790532845L;

    /**
     * 额外操作的类型，枚举值为 submit_tool_outputs。
     */
    @JsonProperty("type")
    private String type;

    /**
     * 需要提交的结果详情，通过提交接口上传，并可以继续聊天
     */
    @JsonProperty("submit_tool_outputs")
    private CozeOutputTool submitToolOutputs;


}
