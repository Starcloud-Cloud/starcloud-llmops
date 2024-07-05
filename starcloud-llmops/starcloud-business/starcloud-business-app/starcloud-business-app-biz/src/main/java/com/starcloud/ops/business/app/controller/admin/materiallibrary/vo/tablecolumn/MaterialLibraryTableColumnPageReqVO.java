package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.tablecolumn;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 素材知识库表格信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaterialLibraryTableColumnPageReqVO extends PageParam {

    @Schema(description = "素材库ID", example = "30175")
    private Long libraryId;

    @Schema(description = "列名", example = "芋艿")
    private String columnName;

    @Schema(description = "类型", example = "1")
    private String columnType;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否必须")
    private Integer isRequired;

    @Schema(description = "序号")
    private String sequence;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}