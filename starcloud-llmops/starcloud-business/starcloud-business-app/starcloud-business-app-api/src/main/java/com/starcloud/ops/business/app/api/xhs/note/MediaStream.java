package com.starcloud.ops.business.app.api.xhs.note;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "视频地址")
public class MediaStream {

    @Schema(description = "视频地址集合")
    private List<StreamDetail> h264;

//    private List<StreamDetail> h265;
//
//    private List<StreamDetail> av1;
}
