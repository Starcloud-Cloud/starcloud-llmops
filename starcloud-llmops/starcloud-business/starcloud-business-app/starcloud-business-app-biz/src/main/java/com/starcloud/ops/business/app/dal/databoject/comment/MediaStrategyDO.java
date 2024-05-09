package com.starcloud.ops.business.app.dal.databoject.comment;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

import java.time.*;
import java.util.*;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 媒体回复策略 DO
 *
 * @author starcloudadmin
 */
@TableName("marketing_media_strategy")
@KeySequence("marketing_media_strategy_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaStrategyDO extends BaseDO {

    /**
     * 策略编号
     */
    @TableId
    private Long id;
    /**
     * 策略名称
     */
    private String name;
    /**
     * 平台类型
     */
    private Integer platformType;
    /**
     * 操作类型
     */
    private Integer actionType;
    /**
     * 关键词匹配
     */
    private Integer keywordMatchType;
    /**
     * 关键词
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> keywordGroups;
    /**
     * 具体操作
     */
    private String actions;
    /**
     * 时机
     */
    private Long intervalTimes;
    /**
     * 频率
     */
    private Integer frequency;
    /**
     * 指定用户组
     */
    private String assignAccount;
    /**
     * 指定作品组
     */
    private String assignMedia;
    /**
     * 生效开始时间
     */
    private LocalTime validStartTime;
    /**
     * 生效结束时间
     */
    private LocalTime validEndTime;
    /**
     * 状态
     */
    private Integer status;

}