package com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response;

import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.BaseMaterialVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "素材详情")
public class MaterialRespVO extends BaseMaterialVO {

    @Schema(description = "素材uid")
    private String uid;

}
