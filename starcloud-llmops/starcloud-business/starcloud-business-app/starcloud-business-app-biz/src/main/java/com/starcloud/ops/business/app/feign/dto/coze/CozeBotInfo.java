package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CozeBotInfo {

    @JsonProperty("bot_id")
    private String botId;

    private String name;

    private String description;

    @JsonProperty("bot_mode")
    private String botMode;

}
