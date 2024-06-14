package com.starcloud.ops.business.poster.controller.admin.materialcategory.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 素材分类分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PosterMaterialCategoryPageReqVO extends PageParam {

    @Schema(description = "父分类编号", example = "20467")
    private Long parentId;

    @Schema(description = "分类名称", example = "王五")
    private String name;

    @Schema(description = "缩略图")
    private String thumbnail;

    @Schema(description = "分类排序")
    private Integer sort;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}