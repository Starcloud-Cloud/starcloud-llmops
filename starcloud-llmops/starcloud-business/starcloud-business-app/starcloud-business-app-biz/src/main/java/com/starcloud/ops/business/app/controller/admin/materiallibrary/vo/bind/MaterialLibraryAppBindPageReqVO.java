package com.starcloud.ops.business.app.controller.admin.materiallibrary.vo.bind;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 应用素材绑定分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MaterialLibraryAppBindPageReqVO extends PageParam {

    @Schema(description = "素材编号", example = "14790")
    private Long libraryId;

    @Schema(description = "应用类型", example = "2")
    private Integer appType;

    @Schema(description = "应用编号", example = "7090")
    private String appUid;

    @Schema(description = "用户编号", example = "23875")
    private Long userId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}