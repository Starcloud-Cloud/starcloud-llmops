package com.starcloud.ops.business.app.feign.cozev2;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WorkflowDataAsynResult {

    @JsonProperty("execute_id")
    private String executeId;

    @JsonProperty("create_time")
    private Long createTime;

    @JsonProperty("token")
    private String token;

    @JsonProperty("logid")
    private String logid;

    @JsonProperty("connector_id")
    private String connectorId;

    @JsonProperty("error_code")
    private String errorCode;

    @JsonProperty("cost")
    private String cost;

    @JsonProperty("update_time")
    private Long updateTime;

    @JsonProperty("execute_status")
    private String executeStatus;

    @JsonProperty("connector_uid")
    private String connectorUid;

    @JsonProperty("run_mode")
    private Integer runMode;

    @JsonProperty("debug_url")
    private String debugUrl;

    @JsonProperty("output")
    private String output;

    @JsonProperty("bot_id")
    private String botId;

    @JsonProperty("error_message")
    private String errorMessage;

}
