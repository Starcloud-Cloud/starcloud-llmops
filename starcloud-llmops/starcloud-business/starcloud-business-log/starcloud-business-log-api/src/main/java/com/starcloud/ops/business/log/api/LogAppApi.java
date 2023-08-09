package com.starcloud.ops.business.log.api;

import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageCreateReqVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageInfoRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageRespVO;
import com.starcloud.ops.business.log.enums.LogStatusEnum;

import java.util.List;

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
     * @param uid        应用会话唯一标识
     * @param statusEnum 应用会话状态
     */
    void updateAppConversationStatus(String uid, LogStatusEnum statusEnum);

    /**
     * 创建日志应用消息
     *
     * @param logAppMessageCreateReqVO 日志应用消息创建请求
     */
    void createAppMessage(LogAppMessageCreateReqVO logAppMessageCreateReqVO);

    /**
     * 获取应用消息结果
     *
     * @param appMessageUid 应用消息唯一标识
     * @return 应用消息结果
     */
    LogAppMessageInfoRespVO getAppMessageResult(String appMessageUid);

}
