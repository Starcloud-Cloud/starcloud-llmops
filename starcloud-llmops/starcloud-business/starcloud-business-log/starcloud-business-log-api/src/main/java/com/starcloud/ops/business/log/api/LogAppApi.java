package com.starcloud.ops.business.log.api;

import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;

public interface LogAppApi {

    LogAppConversationCreateReqVO createAppConversation(LogAppConversationCreateReqVO logAppConversationCreateReqVO);

    void updateAppConversationStatus(String uid, LogStatusEnum statusEnum);

    void createAppMessage(LogAppMessageCreateReqVO logAppMessageCreateReqVO);


    LogAppMessageInfoRespVO getAppMessageResult(String appMessageUid);
}
