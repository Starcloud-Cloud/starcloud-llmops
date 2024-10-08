package com.starcloud.ops.business.mission.dal.dataobject;

import lombok.Data;

@Data
public class NotificationCenterDTO extends NotificationCenterDO{
    /**
     * 总量
     */
    private Long total;

    /**
     * 待领取数
     */
    private Long stayClaimCount;

    /**
     * 领取数
     */
    private Long claimCount;

    /**
     * 用户发布数
     */
    private Long publishedCount;

    /**
     * 完成数
     */
    private Long settlementCount;

    /**
     * 创建人
     */
    private String createUser;
}
