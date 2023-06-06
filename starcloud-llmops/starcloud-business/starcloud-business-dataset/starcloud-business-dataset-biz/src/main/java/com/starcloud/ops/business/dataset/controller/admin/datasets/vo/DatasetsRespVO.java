package com.starcloud.ops.business.dataset.controller.admin.datasets.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 数据集 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetsRespVO extends DatasetsBaseVO {

    @Schema(description = "主键ID", required = true, example = "21796")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

}