package com.starcloud.ops.business.app.api.app.vo.response.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "数据集绑定")
public class DatesetRespVO {

    @Schema(description = "数据集UID")
    private String datasetUid;

    @Schema(description = "开启数据集")
    private Boolean enabled;
}
