package com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response;

import com.starcloud.ops.business.app.enums.xhs.plan.CreativePlanStatusEnum;
import com.starcloud.ops.business.app.model.plan.CreativePlanConfigurationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreativePlanBatchRespVO {

    private Long id;

    /**
     * 执行批次号
     */
    @Schema(description = "执行UID")
    private String uid;

    /**
     * 创作计划uid
     */
    @Schema(description = "创作计划uid")
    private String planUid;

    /**
     * 应用UID
     */
    @Schema(description = "应用UID")
    private String appUid;

    /**
     * 应用版本号
     */
    @Schema(description = "版本号")
    private Integer version;

    /**
     * 创作计划来源
     */
    @Schema(description = "创作计划来源")
    private String source;

    /**
     * 执行计划json
     */
    @Schema(description = "创作计划配置")
    private CreativePlanConfigurationDTO configuration;

    /**
     * 总数
     */
    @Schema(description = "总数")
    private Integer totalCount;

    /**
     * 失败数
     */
    @Schema(description = "失败数")
    private Integer failureCount;

    /**
     * 成功数
     */
    @Schema(description = "成功数")
    private Integer successCount;

    /**
     * 开始执行时间
     */
    @Schema(description = "开始执行时间")
    private LocalDateTime startTime;

    /**
     * 执行结束时间
     */
    @Schema(description = "执行结束时间")
    private LocalDateTime endTime;

    /**
     * 执行耗时
     */
    @Schema(description = "执行耗时")
    private Long elapsed;

    /**
     * 批次状态，执行中，执行结束 {@link CreativePlanStatusEnum}
     */
    @Schema(description = "批次状态")
    private String status;

    /**
     * 批次创建者
     */
    @Schema(description = "批次创建者")
    private String creator;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
