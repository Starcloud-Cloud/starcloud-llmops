package com.starcloud.ops.business.share.controller.app.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "查询应用详情")
public class ChatDetailReqVO {

    @Schema(description = "媒介Uid集合")
    private List<String> mediumUids;
}
