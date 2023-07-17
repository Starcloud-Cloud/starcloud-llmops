package com.starcloud.ops.business.app.service.chat;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
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
    SseEmitter chat(ChatRequestVO request);

    /**
     * 聊天记录
     * @param conversationUid
     * @return
     */
    List<LogAppMessageDO> chatHistory(String conversationUid);

    /**
     * 会话建议
     */
    List<String> chatSuggestion(String conversationUid);

}
