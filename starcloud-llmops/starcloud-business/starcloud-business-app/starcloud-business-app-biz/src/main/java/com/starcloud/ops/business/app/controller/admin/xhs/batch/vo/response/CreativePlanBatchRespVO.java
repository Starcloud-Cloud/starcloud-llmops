package com.starcloud.ops.business.app.controller.admin.xhs.batch.vo.response;

import com.starcloud.ops.business.app.api.xhs.plan.vo.response.CreativePlanRespVO;
import com.starcloud.ops.business.app.api.xhs.scheme.vo.response.CreativeSchemeRespVO;
import com.starcloud.ops.business.app.enums.xhs.batch.CreativePlanBatchStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreativePlanBatchRespVO {

    @Schema(description = "id")
    private Long id;

    /**
     * 执行批次号 时间戳
     */
    @Schema(description = "执行批次号")
    private Long batch;

    /**
     * 创作计划uid
     */
    @Schema(description = "创作计划uid")
    private String planUid;

    /**
     * 执行计划json
     */
    @Schema(description = "执行计划json")
    private CreativePlanRespVO creativePlan;

    /**
     * 创作方案json
     */
    @Schema(description = "创作方案json")
    private List<CreativeSchemeRespVO> schemeConfig;

    /**
     * 批次状态，执行中，执行结束 {@link CreativePlanBatchStatusEnum}
     */
    @Schema(description = "批次状态")
    private String status;

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
     * 总数
     */
    @Schema(description = "总数")
    private Integer totalCount;
}
