package com.starcloud.ops.business.log.api.message.vo.query;

import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "AppLogMessageStatisticsListUidReqVO", description = "应用执行日志统计请求 VO ")
public class AppLogMessageStatisticsListUidReqVO implements Serializable {

    private static final long serialVersionUID = -399148030304256774L;

    /**
     * 应用 UID
     */
    @Schema(description = "应用 UID")
    @NotBlank(message = "应用 UID 不能为空")
    private String appUid;

    /**
     * 应用市场 UID
     */
    @Schema(description = "应用市场 UID", hidden = true)
    private String marketUid;

    /**
     * 应用场景
     */
    @Schema(description = "应用场景")
    private String fromScene;

    /**
     * AI模型
     */
    @Schema(description = "AI模型")
    private String aiModel;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String userId;

    /**
     * 应用状态
     */
    @Schema(description = "应用状态")
    private String status;

    /**
     * 查询时间范围类型
     */
    @Schema(description = "查询时间范围类型")
    @InEnum(value = LogTimeTypeEnum.class, field = InEnum.EnumField.NAME, message = "查询时间范围类型 {value} 不正确, 支持的类型为： {values}")
    private String timeType;

    /**
     * 时间单位
     */
    @Schema(description = "时间单位", hidden = true)
    private String unit;

    /**
     * 创建时间
     */
    @Schema(description = "数据开始时间", hidden = true)
    private String startTime;

    /**
     * 创建时间
     */
    @Schema(description = "数据结束时间", hidden = true)
    private String endTime;

}