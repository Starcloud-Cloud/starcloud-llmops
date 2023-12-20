package com.starcloud.ops.business.user.controller.admin.rights.vo.rights;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminUserRightsCollectRespVO {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "总量")
    private Integer totalNum;

    @Schema(description = "使用量")
    private Integer usedNum;

    @Schema(description = "剩余量")
    private Integer remaining;

    @Schema(description = "权益百分比")
    private double percentage;

}