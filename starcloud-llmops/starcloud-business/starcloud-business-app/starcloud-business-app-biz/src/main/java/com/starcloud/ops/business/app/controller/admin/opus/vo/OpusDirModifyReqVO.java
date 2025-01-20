package com.starcloud.ops.business.app.controller.admin.opus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改目录")
public class OpusDirModifyReqVO extends OpusDirBaseVO{

    @Schema(description = "目录uid")
    private String dirUid;
}
