package com.starcloud.ops.business.app.dal.databoject.xhs.plan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class CreativePlanPO implements java.io.Serializable {

    private static final long serialVersionUID = 6198448765556811129L;

    /**
     * 创作计划ID
     */
    private Long id;

    /**
     * 创作计划UID
     */
    private String uid;

    /**
     * 创作计划名称
     */
    private String name;

    /**
     * 创作计划类型
     */
    private String type;

    /**
     * 创作计划详细配置信息
     */
    private String configuration;

    /**
     * 执行随机方式
     */
    private String randomType;

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failureCount;

    /**
     * 待执行数量
     */
    private Integer pendingCount;

    /**
     * 生成数量
     */
    private Integer total;

    /**
     * 计划开始时间
     */
    private LocalDateTime startTime;

    /**
     * 计划结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行总耗时
     */
    private Long elapsed;

    /**
     * 创作计划描述
     */
    private String description;

    /**
     * 创作计划执行状态：待执行，执行中，暂停，执行完成
     */
    private String status;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String updater;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

}
