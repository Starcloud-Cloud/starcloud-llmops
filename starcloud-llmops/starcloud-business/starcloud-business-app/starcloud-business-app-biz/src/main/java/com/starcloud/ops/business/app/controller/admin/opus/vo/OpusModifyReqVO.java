package com.starcloud.ops.business.app.controller.admin.opus.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改作品集")
public class OpusModifyReqVO extends OpusBaseVO{

    @Schema(description = "作品集uid")
    private String opusUid;
}
