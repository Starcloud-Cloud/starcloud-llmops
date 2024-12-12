package com.starcloud.ops.business.app.feign.cozev2;

import lombok.Data;

@Data
public class WorkflowRunResult {

    private String workflowId;

    private String executeId;

    private String accessToken;

    private String redisKey;

    public WorkflowRunResult(String redisKey, String accessToken, String workflowId) {
        this.redisKey = redisKey;
        this.accessToken = accessToken;
        this.workflowId = workflowId;
    }
}
