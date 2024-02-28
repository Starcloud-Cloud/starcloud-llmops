package com.starcloud.ops.business.app.dal.databoject.xhs.plan;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "llm_creative_plan", autoResultMap = true)
@KeySequence("llm_creative_plan_seq")
public class CreativePlanDO extends TenantBaseDO {

    private static final long serialVersionUID = 8253406024334826647L;

    /**
     * 创作计划ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创作计划UID
     */
    @TableField("uid")
    private String uid;

    /**
     * 创作计划名称
     */
    @TableField("name")
    private String name;

    /**
     * 创作计划类型
     */
    @TableField("type")
    private String type;

    /**
     * 创作计划详细配置信息
     */
    @TableField("configuration")
    private String configuration;

    /**
     * 执行随机方式
     */
    @TableField("random_type")
    private String randomType;

    /**
     * 生成数量
     */
    @TableField("total")
    private Integer total;

    /**
     * 创作计划执行状态：待执行，执行中，暂停，执行完成
     */
    @TableField("status")
    private String status;

    /**
     * 计划开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 计划结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 执行总耗时
     */
    @TableField("elapsed")
    private Long elapsed;

    /**
     * 创作计划描述
     */
    @TableField("description")
    private String description;

    /**
     * 创作计划标签
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> tags;
}
