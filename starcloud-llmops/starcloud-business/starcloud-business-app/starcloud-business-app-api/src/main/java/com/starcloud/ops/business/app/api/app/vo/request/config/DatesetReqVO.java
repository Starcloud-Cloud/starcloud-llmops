package com.starcloud.ops.business.app.api.app.vo.request.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "数据集绑定")
@AllArgsConstructor
@NoArgsConstructor
public class DatesetReqVO {

    @Schema(description = "数据集UID")
    private String datasetUid;

    @Schema(description = "开启数据集")
    private Boolean enabled;
}
