package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BotListInfo {

    @JsonProperty("space_bots")
    private List<SpaceBot> spaceBots;

    private Integer total;
}
