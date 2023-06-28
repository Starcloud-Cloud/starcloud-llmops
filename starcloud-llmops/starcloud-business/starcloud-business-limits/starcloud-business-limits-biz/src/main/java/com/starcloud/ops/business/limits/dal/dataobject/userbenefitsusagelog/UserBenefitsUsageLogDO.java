package com.starcloud.ops.business.limits.dal.dataobject.userbenefitsusagelog;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 用户权益使用日志 DO
 *
 * @author AlanCusack
 */
@TableName("llm_user_benefits_usage_log")
@KeySequence("llm_user_benefits_usage_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBenefitsUsageLogDO extends TenantBaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 策略编号
     */
    private String uid;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 操作类型(使用、过期、增加对应字典)
     */
    private String action;
    /**
     * 权益类型（例如：应用数、数据集数、图片数、Token数）
     */
    private String benefitsType;
    /**
     * 权益数量变化（正数表示增加，负数表示减少）
     */
    private Long amount;
    /**
     * 应用程序ID或者数据集ID
     */
    private String outId;
    /**
     * 用户权益编号（单条权益不够，扣除其他策略下）
     */
    private String benefitsIds;
    /**
     * 使用时间
     */
    private LocalDateTime usageTime;

}
