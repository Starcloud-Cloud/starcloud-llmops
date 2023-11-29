package com.starcloud.ops.business.mission.dal.dataobject;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@TableName("llm_single_mission")
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SingleMissionDO extends TenantBaseDO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String uid;

    /**
     * 通告Uid
     */
    private String notificationUid;

    /**
     * 创作任务Uid
     */
    private String creativeUid;

    /**
     * 任务类型 {@link com.starcloud.ops.business.enums.MisssionTypeEnum}
     */
    private String type;

    /**
     * 任务内容 json
     */
    private String content;

    /**
     * 状态 {@link com.starcloud.ops.business.enums.SingleMissionStatusEnum}
     */
    private String status;

    /**
     * 认领人Id
     */
    private String claimUserId;

    /**
     * 认领人
     */
    private String claimUsername;

    /**
     * 认领时间
     */
    private LocalDateTime claimTime;

    /**
     * 发布链接
     */
    private String publishUrl;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 预结算时间
     */
    private LocalDateTime preSettlementTime;

    /**
     * 预估花费
     */
    private BigDecimal estimatedAmount;

    /**
     * 结算时间
     */
    private LocalDateTime settlementTime;

    /**
     * 结算金额
     */
    private BigDecimal settlementAmount;

    /**
     * 支付单号
     */
    private String paymentOrder;

    /**
     * 定时执行时间
     */
    private LocalDateTime runTime;


}
