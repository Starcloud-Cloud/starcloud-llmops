package com.starcloud.ops.business.app.dal.databoject.xhs.content;

import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import com.starcloud.ops.business.app.enums.xhs.content.CreativeContentStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author nacoyer
 * @version 1.0.0
 * @since 2023-11-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(value = "llm_creative_content", autoResultMap = true)
@KeySequence("llm_creative_content_seq")
public class CreativeContentDO extends DeptBaseDO {

    private static final long serialVersionUID = 5941580301331021336L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * UID
     */
    @TableField("uid")
    private String uid;

    /**
     * 执行批次UID
     */
    @TableField("batch_uid")
    private String batchUid;

    /**
     * 计划uid
     */
    @TableField("plan_uid")
    private String planUid;

    /**
     * 会话UID
     */
    @TableField("conversation_uid")
    private String conversationUid;

    /**
     * 内容类型
     */
    @TableField("type")
    private String type;

    /**
     * 内容来源
     */
    @TableField("source")
    private String source;

    /**
     * 执行请求参数
     */
    @TableField("execute_param")
    private String executeParam;

    /**
     * 执行响应结果
     */
    @TableField("execute_result")
    private String executeResult;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 执行耗时
     */
    @TableField("elapsed")
    private Long elapsed;

    /**
     * 执行状态 {@link CreativeContentStatusEnum}
     */
    @TableField("status")
    private String status;

    /**
     * 重试次数
     */
    @TableField("retry_count")
    private Integer retryCount;

    /**
     * 失败信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 是否喜欢
     */
    @TableField("liked")
    private Boolean liked;

    /**
     * 是否绑定
     */
    @TableField("claim")
    private Boolean claim;


}
