package com.starcloud.ops.business.mission.api.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(description = "用户详情")
@AllArgsConstructor
public class UserDetailVO {

    @Schema(description = "name")
    private String username;

    @Schema(description = "发布通告数")
    private Long notificationCount;

    @Schema(description = "头像")
    private String avatar;

}
