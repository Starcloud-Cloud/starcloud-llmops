package com.starcloud.ops.business.log.api.conversation.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Schema(name = "LogAppConversationInfoPageReqVO", description = "应用会话日志分页查询请求 VO")
public class LogAppConversationInfoPageReqVO extends PageParam {

    private static final long serialVersionUID = -6444036479357643539L;

    /**
     * 应用 UID
     */
    @Schema(description = "应用 UID")
    private String appUid;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String appName;

    /**
     * 应用模型
     */
    @Schema(description = "应用模型")
    private String appMode;

    /**
     * 应用场景
     */
    @Schema(description = "应用场景")
    private String fromScene;

    /**
     * 应用状态
     */
    @Schema(description = "应用状态")
    private String status;

    /**
     * 用户
     */
    @Schema(description = "用户")
    private String user;

    /**
     * 终端用户
     */
    @Schema(description = "终端用户")
    private String endUser;

    /**
     * 查询时间范围类型
     *
     * @see com.starcloud.ops.business.log.enums.LogTimeTypeEnum
     */
    @Schema(description = "查询时间范围类型")
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