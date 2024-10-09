package com.starcloud.ops.business.mission.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.iocoder.yudao.framework.tenant.core.db.DeptBaseDO;
import com.starcloud.ops.business.enums.NotificationCenterStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("llm_notification")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NotificationCenterDO extends DeptBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String uid;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 平台
     */
    private String platform;

    /**
     * 领域
     */
    private String field;

    /**
     * 类型 {@link com.starcloud.ops.business.enums.MisssionTypeEnum}
     */
    private String type;

    /**
     * 状态 {@link NotificationCenterStatusEnum}
     */
    private String status;

    /**
     * 单价 json
     */
    private String unitPrice;

    /**
     * 任务开始时间
     */
    private LocalDateTime startTime;

    /**
     * 任务结束时间
     */
    private LocalDateTime endTime;

    /**
     * 任务总预算
     */
    private BigDecimal notificationBudget;

    /**
     * 单个任务预算
     */
    private BigDecimal singleBudget;

    /**
     * 任务说明
     */
    private String description;

    /**
     * 备注
     */
    private String remark;

    /**
     * 领取人员限制
     */
    private String claimLimit;

    /**
     * 最小粉丝数
     */
    private Integer minFansNum;

    /**
     * 访问次数
     */
    private Integer visitNum;

    /**
     * 是否公开
     */
    private Boolean open;

}
