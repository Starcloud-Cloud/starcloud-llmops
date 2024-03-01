package com.starcloud.ops.business.app.api.xhs.plan.vo.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starcloud.ops.business.app.api.xhs.plan.dto.CreativePlanConfigurationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CreativePlanRespVO", description = "执行计划响应")
public class CreativePlanRespVO implements java.io.Serializable {

    private static final long serialVersionUID = -8046050421435541629L;

    /**
     * 创作计划UID
     */
    @Schema(description = "创作计划UID")
    private String uid;

    /**
     * 创作计划名称
     */
    @Schema(description = "创作计划名称")
    private String name;

    /**
     * 执行批次
     */
    @Schema(description = "执行批次")
    private Long batch;

    /**
     * 创作计划类型
     */
    @Schema(description = "创作计划类型")
    private String type;

    /**
     * 创作计划详细配置信息
     */
    @Schema(description = "创作计划详细配置信息")
    private CreativePlanConfigurationDTO configuration;

    /**
     * 成功数量
     */
    @Schema(description = "成功数量")
    private Integer successCount;

    /**
     * 失败数量
     */
    @Schema(description = "失败数量")
    private Integer failureCount;

    /**
     * 待执行数量
     */
    @Schema(description = "待执行数量")
    private Integer pendingCount;

    /**
     * 执行随机方式
     */
    @Schema(description = "执行随机方式")
    private String randomType;

    /**
     * 生成数量
     */
    @Schema(description = "生成数量")
    private Integer total;

    /**
     * 创作计划执行状态：待执行，执行中，暂停，执行完成
     */
    @Schema(description = "创作计划执行状态")
    private String status;

    /**
     * 计划开始时间
     */
    @Schema(description = "计划开始时间")
    private LocalDateTime startTime;

    /**
     * 计划结束时间
     */
    @Schema(description = "计划结束时间")
    private LocalDateTime endTime;

    /**
     * 执行总耗时
     */
    @Schema(description = "执行总耗时")
    private Long elapsed;

    /**
     * 创作计划描述
     */
    @Schema(description = "创作计划描述")
    private String description;

    /**
     * 创建人
     */
    @Schema(description = "计划创建者")
    private String creator;

    /**
     * 修改人
     */
    @Schema(description = "计划修改者")
    private String updater;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    /**
     * 标签
     */
    @Schema(description = "标签")
    private List<String> tags;
}
