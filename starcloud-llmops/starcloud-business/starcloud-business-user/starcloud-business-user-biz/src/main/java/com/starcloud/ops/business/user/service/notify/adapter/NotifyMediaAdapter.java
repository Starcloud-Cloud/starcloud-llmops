package com.starcloud.ops.business.user.service.notify.adapter;

import com.starcloud.ops.business.user.controller.admin.notify.dto.SendNotifyReqDTO;
import com.starcloud.ops.business.user.controller.admin.notify.dto.SendNotifyResultDTO;
import com.starcloud.ops.business.user.enums.notify.NotifyMediaEnum;

public interface NotifyMediaAdapter {

    /**
     * 媒介类型
     *
     * @return
     */
    NotifyMediaEnum supportType();

    /**
     * 发送通知
     *
     * @param sendNotifyReqDTO
     * @return
     */
    SendNotifyResultDTO sendNotify(SendNotifyReqDTO sendNotifyReqDTO);

    /**
     * 更新站内信日志
     *
     * @param logId
     * @param resultDTO
     */
    void updateLog(Long logId, SendNotifyResultDTO resultDTO);
}