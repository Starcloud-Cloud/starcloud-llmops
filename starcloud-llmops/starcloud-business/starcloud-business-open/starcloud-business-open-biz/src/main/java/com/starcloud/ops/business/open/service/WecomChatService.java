package com.starcloud.ops.business.open.service;

import com.starcloud.ops.business.open.controller.admin.vo.request.QaCallbackReqVO;

public interface WecomChatService {

    /**
     * 异步回复群消息
     * @param qaMsgRequest
     */
    void asynReplyMsg(QaCallbackReqVO qaMsgRequest);

    /**
     * 发送群消息
     * @param groupRemark
     * @param msg
     * @param endUser
     */
    void sendMsg(String groupRemark, String msg, String endUser);
}
