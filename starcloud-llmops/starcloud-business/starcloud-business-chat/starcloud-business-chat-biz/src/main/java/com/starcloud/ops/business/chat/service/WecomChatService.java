package com.starcloud.ops.business.chat.service;

import com.starcloud.ops.business.chat.controller.admin.wecom.vo.request.QaCallbackReqVO;

public interface WecomChatService {

    /**
     * 异步回复群消息
     * @param qaMsgRequest
     */
    void asynReplyMsg(QaCallbackReqVO qaMsgRequest);
}
