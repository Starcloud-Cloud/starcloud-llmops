package com.starcloud.ops.business.app.dal.databoject.xhs.plan;

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
public class CreativePlanDO extends DeptBaseDO {

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
     * 创作计划执行状态：待执行，执行中，暂停，执行完成
     */
    @TableField("status")
    private String status;

}
