package com.starcloud.ops.business.user.api.level.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperateDTO {

    /**
     * 是否添加
     */
    @Schema(description = "权益是否添加", example = " 1")
    private boolean isAdd;

    /**
     * 是否叠加
     */
    @Schema(description = "权益是否叠加", example = " 1")
    private boolean isSuperposition;

}
