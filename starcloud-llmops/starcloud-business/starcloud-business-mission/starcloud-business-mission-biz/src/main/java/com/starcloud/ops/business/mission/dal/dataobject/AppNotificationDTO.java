package com.starcloud.ops.business.mission.dal.dataobject;

import lombok.Data;

@Data
public class AppNotificationDTO extends NotificationCenterDO{

    /**
     * 总领取数
     */
    private Integer claimCount;

    /**
     * 当前用户领取数量
     */
    private Integer currentUserNum;

    /**
     * 任务uid
     */
    private String messionUids;

    /**
     * 图片
     */
    private String picture;
}
