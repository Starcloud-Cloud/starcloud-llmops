package com.starcloud.ops.business.job.biz.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starcloud.ops.business.job.biz.enums.BusinessJobTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@TableName("llm_business_job")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BusinessJobDO extends DeptBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务id
     */
    private Long jobId;

    /**
     * 名称
     */
    private String name;

    /**
     * uid
     */
    private String uid;

    /**
     * 描述
     */
    private String descption;

    /**
     * 外键
     */
    private String foreignKey;

    /**
     * 执行参数 字段映射
     */
    private String config;

    /**
     * 定时类型
     */
    private Integer timeExpressionType;

    /**
     * 定时表达式
     */
    private String timeExpression;

    /**
     * 任务的业务类型 {@link BusinessJobTypeEnum}
     */
    private String businessJobType;

    /**
     * 生命周期开始时间
     */
    private Long lifecycleStart;

    /**
     * 生命周期结束时间
     */
    private Long lifecycleEnd;

    /**
     * 启用/禁用  默认启用
     */
    private Boolean enable;

}
