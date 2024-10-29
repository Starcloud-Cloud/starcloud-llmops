package com.starcloud.ops.business.app.feign.dto.coze;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SpaceDetail {

    private String id;

    private String name;

    @JsonProperty("icon_url")
    private String iconUrl;

    @JsonProperty("role_type")
    private String roleType;

    @JsonProperty("workspace_type")
    private String workspaceType;
}
