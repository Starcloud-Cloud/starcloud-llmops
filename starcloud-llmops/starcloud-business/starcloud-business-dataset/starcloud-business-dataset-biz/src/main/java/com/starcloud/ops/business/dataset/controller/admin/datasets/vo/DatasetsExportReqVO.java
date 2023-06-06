package com.starcloud.ops.business.dataset.controller.admin.datasets.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 数据集 Excel 导出 Request VO，参数和 DatasetsPageReqVO 是一致的")
@Data
public class DatasetsExportReqVO {

    @Schema(description = "数据集编号", example = "23663")
    private String uid;

    @Schema(description = "名称", example = "李四")
    private String name;

    @Schema(description = "描述", example = "你说的对")
    private String description;

    @Schema(description = "提供商")
    private String provider;

    @Schema(description = "权限 (0-私有，1-租户共享，2-全体共享)")
    private Integer permission;

    @Schema(description = "数据源类型", example = "2")
    private String sourceType;

    @Schema(description = "索引技术")
    private String indexingModel;

    @Schema(description = "索引结构")
    private String indexStruct;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "是否启用")
    private Boolean enabled;

}