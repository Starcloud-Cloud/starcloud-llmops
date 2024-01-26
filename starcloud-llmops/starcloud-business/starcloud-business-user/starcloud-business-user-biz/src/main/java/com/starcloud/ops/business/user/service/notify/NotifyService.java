package com.starcloud.ops.business.user.service.notify;

import com.starcloud.ops.business.user.controller.admin.notify.vo.CreateNotifyReqVO;
import com.starcloud.ops.business.user.controller.admin.notify.vo.NotifyContentRespVO;
import com.starcloud.ops.framework.common.api.dto.Option;

import java.util.List;
import java.util.Map;

public interface NotifyService {

    Map<String, List<Option>> metaData();

    /**
     * 发送通知
     *
     * @param
     */
    void sendNotify(Long logId);

    /**
     * 创建发送消息任务
     * @param reqDTO
     */
    void createMsgTask(CreateNotifyReqVO reqDTO);

    void triggerNotify(CreateNotifyReqVO reqDTO);

    /**
     * 过滤用户通知内容
     * @param templateCode
     * @return
     */
    List<NotifyContentRespVO> filterUser(String templateCode);
}
