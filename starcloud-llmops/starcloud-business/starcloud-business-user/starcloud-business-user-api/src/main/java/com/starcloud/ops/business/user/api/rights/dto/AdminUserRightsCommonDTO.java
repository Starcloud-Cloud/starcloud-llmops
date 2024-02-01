package com.starcloud.ops.business.user.api.rights.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AdminUserRightsCommonDTO {

    @Schema(description = "用户基础权益")
    private UserRightsBasicDTO rightsBasicDTO;

    @Schema(description = "用户等级权益")
    private UserLevelBasicDTO levelBasicDTO;

}
