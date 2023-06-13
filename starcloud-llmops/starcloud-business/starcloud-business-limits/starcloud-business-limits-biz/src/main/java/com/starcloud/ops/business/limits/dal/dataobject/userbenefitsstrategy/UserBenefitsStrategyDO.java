package com.starcloud.ops.business.limits.dal.dataobject.userbenefitsstrategy;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户权益策略表
 DO
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
     * 策略类型枚举
     *
     * 枚举
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
     * 枚举
     */
    private String scope;
    /**
     * 适用时间
     */
    private Integer scopeNum;
    /**
     * 限制兑换次数（-1，不设限制）
     */
    private Long limitUnit;
    /**
     * 是否启用
     */
    private Boolean enabled;
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

}