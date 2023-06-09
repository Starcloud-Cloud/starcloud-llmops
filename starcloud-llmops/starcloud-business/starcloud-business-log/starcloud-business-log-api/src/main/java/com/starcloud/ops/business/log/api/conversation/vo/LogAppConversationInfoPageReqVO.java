package com.starcloud.ops.business.log.api.conversation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.date.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LogAppConversationInfoPageReqVO extends PageParam {

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
    //@DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "数据开始时间")
    private LocalDateTime startTime;

    /**
     * 创建时间
     */
    //@DateTimeFormat(pattern = DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "数据结束时间")
    private LocalDateTime endTime;

}