package com.starcloud.ops.business.poster.controller.admin.material.vo;

import com.starcloud.ops.business.app.controller.admin.app.vo.AppModifyExtendReqVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "素材应用")
@Data
public class MaterialAppReqVO extends AppModifyExtendReqVO {

    private String styleUid;

    private String templateUid;

    private String templateCode;


}