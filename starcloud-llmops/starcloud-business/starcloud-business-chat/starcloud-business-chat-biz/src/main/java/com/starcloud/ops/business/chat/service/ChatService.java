package com.starcloud.ops.business.chat.service;

import com.starcloud.ops.business.chat.request.ChatRequest;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * @author starcloud
 */
public interface ChatService {

    /**
     * 对话
     * @param request
     * @return
     */
    SseEmitter chat(ChatRequest request);

    /**
     * 聊天记录
     * @param conversationUid
     * @return
     */
    List<LogAppMessageDO> chatHistory(String conversationUid);
}
