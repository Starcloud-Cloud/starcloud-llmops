package com.starcloud.ops.business.log.api.message.vo.query;

import com.starcloud.ops.business.log.enums.LogTimeTypeEnum;
import com.starcloud.ops.framework.common.api.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-06-25
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Schema(name = "AppLogMessageStatisticsListReqVO", description = "应用执行日志统计请求 VO ")
public class AppLogMessageStatisticsListReqVO implements Serializable {

    private static final long serialVersionUID = -37371739526283469L;
    
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
     * 应用场景列表
     */
    @Schema(hidden = true)
    private List<String> fromSceneList;

    /**
     * 查询时间范围类型
     */
    @Schema(description = "查询时间范围类型")
    @InEnum(value = LogTimeTypeEnum.class, field = InEnum.EnumField.NAME, message = "查询时间范围类型 {value}, 支持的类型为 {values}")
    private String timeType;

    /**
     * 时间单位
     */
    @Schema(hidden = true)
    private String unit;

    /**
     * 创建时间
     */
    @Schema(description = "数据开始时间")
    private String startTime;

    /**
     * 创建时间
     */
    @Schema(description = "数据结束时间")
    private String endTime;

}