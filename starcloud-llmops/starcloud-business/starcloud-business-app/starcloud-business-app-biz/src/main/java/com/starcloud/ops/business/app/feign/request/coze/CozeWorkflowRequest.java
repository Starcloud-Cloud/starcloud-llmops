package com.starcloud.ops.business.app.feign.request.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CozeWorkflowRequest {

    @JsonProperty("workflow_id")
    private String workflowId;

    private Object parameters;
}
