package com.starcloud.ops.business.poster.controller.admin.materialgroup.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 海报素材分组分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaterialGroupPageReqVO extends PageParam {

    @Schema(description = "编号", example = "6110")
    private String uid;

    @Schema(description = "名称", example = "赵六")
    private String name;

    @Schema(description = "用户类型", example = "1")
    private String userType;

    @Schema(description = "素材分类编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "881")
    private Long categoryId;

    @Schema(description = "是否公开", requiredMode = Schema.RequiredMode.REQUIRED, example = "881")
    private Boolean overtStatus;

    @Schema(description = "分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "881")
    private Integer type;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}