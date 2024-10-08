package com.starcloud.ops.business.log.api;

import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.conversation.vo.request.LogAppConversationStatusReqVO;
import com.starcloud.ops.business.log.api.message.vo.request.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.response.LogAppMessageInfoRespVO;

/**
 * @author admin
 * @version 1.0.0
 * @since 2023-07-24
 */
public interface LogAppApi {

    /**
     * 创建应用会话
     *
     * @param logAppConversationCreateReqVO 应用会话创建请求
     * @return 应用会话创建响应
     */
    LogAppConversationCreateReqVO createAppConversation(LogAppConversationCreateReqVO logAppConversationCreateReqVO);

    /**
     * 更新应用会话状态
     *
     * @param request 应用会话状态更新请求
     */
    void updateAppConversationStatus(LogAppConversationStatusReqVO request);

    /**
     * 创建日志应用消息
     *
     * @param request 日志应用消息创建请求
     */
    String createAppMessage(LogAppMessageCreateReqVO request);

    /**
     * 获取应用消息结果
     *
     * @param appMessageUid 应用消息唯一标识
     * @return 应用消息结果
     */
    LogAppMessageInfoRespVO getAppMessageResult(String appMessageUid);

}
