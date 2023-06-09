package com.starcloud.ops.business.log.api.message.vo;

import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 应用执行日志统计结果")
@Data
@ToString(callSuper = true)
public class LogAppMessageStatisticsListReqVO {

    private String appName;

    private String appUid;

    private String fromScene;

    private String status;

    private String user;

    private String endUser;


    /**
     * 查询时间范围类型
     *
     * @see com.starcloud.ops.business.log.enums.LogTimeTypeEnum
     */
    private String timeType;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "数据开始时间")
    private LocalDateTime startTime;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "数据结束时间")
    private LocalDateTime endTime;


}