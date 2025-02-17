package com.starcloud.ops.business.user.api.rights.dto;


import com.starcloud.ops.business.user.api.level.dto.LevelConfigDTO;
import com.starcloud.ops.business.user.api.level.dto.UserLevelBasicDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserRightsAndLevelCommonDTO {

    @Schema(description = "用户基础权益")
    private UserRightsBasicDTO rightsBasicDTO;

    @Schema(description = "用户等级权益")
    private UserLevelBasicDTO levelBasicDTO;

}
