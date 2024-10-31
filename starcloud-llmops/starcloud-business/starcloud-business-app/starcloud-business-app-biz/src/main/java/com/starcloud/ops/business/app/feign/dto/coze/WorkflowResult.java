package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WorkflowResult {

    @JsonProperty("content_type")
    private Integer contentType;

    @JsonProperty("data")
    private String data;

    @JsonProperty("original_result")
    private String originalResult;

    @JsonProperty("type_for_model")
    private Integer typeForModel;
}
