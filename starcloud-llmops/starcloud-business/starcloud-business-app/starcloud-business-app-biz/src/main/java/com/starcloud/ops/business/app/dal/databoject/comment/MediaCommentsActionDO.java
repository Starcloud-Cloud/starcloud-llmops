package com.starcloud.ops.business.app.dal.databoject.comment;

import lombok.*;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 媒体评论操作 DO
 *
 * @author starcloudadmin
 */
@TableName("marketing_media_comments_action")
@KeySequence("marketing_media_comments_action_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaCommentsActionDO extends BaseDO {

    /**
     * 操作编号
     */
    @TableId
    private Long id;
    /**
     * 评论编号
     */
    private Long commentsId;
    /**
     * 策略编号
     */
    private Long strategyId;
    /**
     * 操作类型
     */
    private Integer actionType;
    /**
     * 执行类型 (手动/自动)
     */
    private Integer executeType;
    /**
     * 执行内容
     */
    private String executeContent;
    /**
     * 执行对象
     */
    private String executeObject;

    /**
     * 预计延迟时间（秒）
     */
    private Long intervalTimes;

    /**
     * 预计执行时间
     */
    private LocalDateTime estimatedExecutionTime;

    /**
     * 实际执行时间
     */
    private LocalDateTime actualExecutionTime;

}