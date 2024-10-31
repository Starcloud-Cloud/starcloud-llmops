package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SpaceListInfo {

    @JsonProperty("total_count")
    private Integer totalCount;

    private List<SpaceDetail> workspaces;
}
