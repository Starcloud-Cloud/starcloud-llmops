package com.starcloud.ops.business.dataset.controller.admin.datasetsourcedata.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 数据集源数据 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DatasetSourceDataRespVO extends DatasetSourceDataBaseVO {

    @Schema(description = "主键ID", required = true, example = "4784")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

}