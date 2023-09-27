package com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户权益策略表
 * DO
 *
 * @author AlanCusack
 */
@TableName("llm_user_benefits_strategy")
@KeySequence("llm_user_benefits_strategy_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBenefitsStrategyDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 是否归档
     */
    private Boolean archived;
    /**
     * 归档人
     */
    private String archivedBy;
    /**
     * 归档时间
     */
    private LocalDateTime archivedTime;
    /**
     * 兑换码
     */
    private String code;
    /**
     * 策略名称
     */
    private String strategyName;
    /**
     * 策略描述
     */
    private String strategyDesc;
    /**
     * 权益类型（字典中管理）
     */
    private String strategyType;
    /**
     * 应用数
     */
    private Long appCount;
    /**
     * 数据集数
     */
    private Long datasetCount;
    /**
     * 图片数
     */
    private Long imageCount;
    /**
     * 令牌数
     */
    private Long tokenCount;

    /**
     * 算力值
     */
    private Long computationalPowerCount;

    /**
     * 有效时间单位范围（-1，不设限制）
     */
    private String effectiveUnit;
    /**
     * 有效时间数
     */
    private Long effectiveNum;
    /**
     * 限制兑换次数
     */
    private Long limitNum;
    /**
     * 限制间隔多久可用（-1，不设限制）
     */
    private String limitIntervalUnit;
    /**
     * 限制兑换次数（-1，不设限制）
     */
    private Long limitIntervalNum;
    /**
     * 是否启用
     */
    private Boolean enabled;

}