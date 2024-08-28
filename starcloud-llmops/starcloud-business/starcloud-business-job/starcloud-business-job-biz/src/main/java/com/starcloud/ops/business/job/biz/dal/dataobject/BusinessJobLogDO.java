package com.starcloud.ops.business.job.biz.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.starcloud.ops.business.job.biz.enums.TriggerTypeEnum;
import lombok.*;

import java.time.LocalDateTime;

@TableName("llm_job_log")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BusinessJobLogDO extends TenantBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String uid;

    /**
     * powerjob任务id
     */
    private Long jobId;

    /**
     * 任务uid
     */
    private String businessJobUid;

    /**
     *触发类型 {@link TriggerTypeEnum}
     */
    private Integer triggerType;

    /**
     * 触发时间
     */
    private LocalDateTime triggerTime;

    /**
     * 任务config
     */
    private String executeConfig;

    /**
     * 结果
     */
    private String executeResult;

    /**
     * 耗时
     */
    private Long executeTime;

    /**
     * 是否成功
     */
    private Boolean success;


}
