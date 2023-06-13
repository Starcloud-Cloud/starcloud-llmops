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
     * 编号
     */
    private String uid;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 策略编号
     */
    private String strategyId;
    /**
     * 应用数
     */
    private Long appCountUsed;
    /**
     * 数据集数
     */
    private Long datasetCountUsed;
    /**
     * 图片数
     */
    private Long imageCountUsed;
    /**
     * 令牌数
     */
    private Long tokenCountUsed;
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

