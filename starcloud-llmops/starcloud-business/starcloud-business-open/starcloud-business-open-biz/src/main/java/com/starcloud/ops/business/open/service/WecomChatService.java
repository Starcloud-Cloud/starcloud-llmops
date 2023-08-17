package com.starcloud.ops.business.open.service;

import com.starcloud.ops.business.open.controller.admin.vo.QaCallbackReqVO;

public interface WecomChatService {

    /**
     * 异步回复群消息
     * @param qaMsgRequest
     */
    void asynReplyMsg(QaCallbackReqVO qaMsgRequest);
}
