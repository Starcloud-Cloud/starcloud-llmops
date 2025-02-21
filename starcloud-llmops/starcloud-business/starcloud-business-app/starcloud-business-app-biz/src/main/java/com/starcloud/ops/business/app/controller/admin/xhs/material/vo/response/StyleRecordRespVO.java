package com.starcloud.ops.business.app.controller.admin.xhs.material.vo.response;

import com.starcloud.ops.business.app.model.poster.PosterStyleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "风格使用记录")
public class StyleRecordRespVO {

    @Schema(description = "记录uid")
    private String uid;

    @Schema(description = "风格uid")
    private String styleUid;

    @Schema(description = "风格详情")
    private PosterStyleDTO posterStyle;

    @Schema(description = "绑定时间")
    private LocalDateTime createTime;
}
