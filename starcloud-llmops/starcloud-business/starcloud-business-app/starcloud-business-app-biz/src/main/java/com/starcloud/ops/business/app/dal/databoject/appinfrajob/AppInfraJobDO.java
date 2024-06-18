package com.starcloud.ops.business.app.dal.databoject.appinfrajob;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 应用定时执行任务 DO
 *
 * @author starcloudadmin
 */
@TableName("llm_app_infra_job")
@KeySequence("llm_app_infra_job_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInfraJobDO extends BaseDO {

    /**
     * 任务编号
     */
    @TableId
    private Long id;
    /**
     * 任务名称
     */
    private String name;
    /**
     * 应用来源
     */
    private Integer appFrom;
    /**
     * 应用编号
     */
    private Integer appUid;
    /**
     * CRON 表达式
     */
    private String cronExpression;
    /**
     * 任务状态
     */
    private Integer status;

}