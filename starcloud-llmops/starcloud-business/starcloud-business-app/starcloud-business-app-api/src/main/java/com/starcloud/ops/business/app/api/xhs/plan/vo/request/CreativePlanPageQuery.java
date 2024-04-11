package com.starcloud.ops.business.app.api.xhs.plan.vo.request;

import com.starcloud.ops.framework.common.api.dto.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "CreativePlanPageQuery", description = "创作计划分页请求")
public class CreativePlanPageQuery extends PageQuery {

    private static final long serialVersionUID = 2680070754414480259L;

    /**
     * 创作计划uid
     */
    @Schema(description = "创作计划uid")
    private String uid;

    /**
     * 创作计划状态
     */
    @Schema(description = "创作计划状态")
    private String status;

    /**
     * 创建开始时间
     */
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime startTime;

    /**
     * 创建结束时间
     */
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime endTime;
}
