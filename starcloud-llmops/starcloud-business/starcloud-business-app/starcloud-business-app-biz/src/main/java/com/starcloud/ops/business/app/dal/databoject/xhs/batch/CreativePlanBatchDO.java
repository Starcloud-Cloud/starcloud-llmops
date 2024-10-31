package com.starcloud.ops.business.app.dal.databoject.xhs.batch;

import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 创作计划批次
 *
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "llm_creative_plan_batch", autoResultMap = true)
@KeySequence("llm_creative_plan_batch_seq")
public class CreativePlanBatchDO extends DeptBaseDO {

    private static final long serialVersionUID = -3967506144564738057L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 执行批次UID
     */
    @TableField("uid")
    private String uid;

    /**
     * 创作计划uid
     */
    @TableField("plan_uid")
    private String planUid;

    /**
     * 应用UID
     */
    @TableField("app_uid")
    private String appUid;

    /**
     * 应用版本号
     */
    @TableField("version")
    private Integer version;

    /**
     * 创作计划来源
     */
    @TableField("source")
    private String source;

    /**
     * 应用信息
     */
    @TableField("configuration")
    private String configuration;

    /**
     * 生成数量
     */
    @TableField("total_count")
    private Integer totalCount;

    /**
     * 失败数量
     */
    @TableField("failure_count")
    private Integer failureCount;

    /**
     * 成功数量
     */
    @TableField("success_count")
    private Integer successCount;

    /**
     * 开始执行时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 执行结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 执行耗时
     */
    @TableField("elapsed")
    private Long elapsed;

    /**
     * 创作计划执行状态：待执行，执行中，暂停，执行完成
     */
    @TableField("status")
    private String status;

}
