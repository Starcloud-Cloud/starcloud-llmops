package com.starcloud.ops.business.limits.dal.dataobject.userbenefits;


import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户权益 DO
 *
 * @author AlanCusack
 */
@TableName("llm_user_benefits")
@KeySequence("llm_user_benefits_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBenefitsDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * 权益编号
     */
    private String uid;
    /**
     * 策略ID
     */
    private String strategyId;

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 可使用应用数
     */
    private Long appCountUsed;
    /**
     * 可使用数据集数
     */
    private Long datasetCountUsed;
    /**
     * 可使用图片数
     */
    private Long imageCountUsed;
    /**
     * 可使用令牌数
     */
    private Long tokenCountUsed;
    /**
     * 赠送令牌数
     */
    private Long tokenCountInit;
    /**
     * 赠送图片数
     */
    private Long imageCountInit;
    /**
     * 赠送应用数
     */
    private Long datasetCountInit;
    /**
     * 赠送应用数
     */
    private Long appCountInit;

    /**
     * 生效时间
     */
    private LocalDateTime effectiveTime;
    /**
     * 过期时间
     */
    private LocalDateTime expirationTime;
    /**
     * 是否启用
     */
    private Boolean enabled;
}

