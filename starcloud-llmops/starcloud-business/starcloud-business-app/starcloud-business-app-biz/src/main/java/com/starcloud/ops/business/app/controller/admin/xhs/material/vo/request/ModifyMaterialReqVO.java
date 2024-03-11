package com.starcloud.ops.business.app.controller.admin.xhs.material.vo.request;

import com.starcloud.ops.business.app.controller.admin.xhs.material.vo.BaseMaterialVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "修改素材")
public class ModifyMaterialReqVO extends BaseMaterialVO {

    @Schema(description = "uid")
    @NotBlank(message = "素材uid不能为空")
    private String uid;

}
