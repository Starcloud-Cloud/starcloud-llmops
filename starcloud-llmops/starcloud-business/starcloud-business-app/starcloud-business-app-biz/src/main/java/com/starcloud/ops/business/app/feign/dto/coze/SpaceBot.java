package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SpaceBot {

    @JsonProperty("bot_id")
    private String botId;

    @JsonProperty("bot_name")
    private String name;

    private String description;

    @JsonProperty("icon_url")
    private String iconUrl;
}
