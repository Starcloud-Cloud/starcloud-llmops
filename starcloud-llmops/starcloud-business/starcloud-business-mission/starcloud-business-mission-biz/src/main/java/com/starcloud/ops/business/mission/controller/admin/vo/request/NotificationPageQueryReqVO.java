package com.starcloud.ops.business.mission.controller.admin.vo.request;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@Schema(description = "分页查询")
public class NotificationPageQueryReqVO extends PageParam {

    @Schema(description = "通告名称")
    private String name;

    @Schema(description = "通告类目")
    private String field;

    @Schema(description = "通告状态")
    private String status;

    @Schema(description = "创建开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createStartTime;

    @Schema(description = "创建结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime createEndTime;
}
