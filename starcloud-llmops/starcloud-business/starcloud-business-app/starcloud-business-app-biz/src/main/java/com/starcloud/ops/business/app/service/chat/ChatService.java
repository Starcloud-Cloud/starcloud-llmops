package com.starcloud.ops.business.app.service.chat;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatHistoryPageQuery;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.log.api.conversation.vo.LogAppConversationRespVO;
import com.starcloud.ops.business.log.api.message.vo.LogAppMessageRespVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import com.starcloud.ops.framework.common.api.dto.PageResp;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.InputStream;
import java.util.List;

/**
 * @author starcloud
 */
public interface ChatService {

    /**
     * 对话
     *
     * @param request
     * @return
     */
    SseEmitter chat(ChatRequestVO request);

    /**
     * 创建聊天应用
     *
     * @param uid
     * @param name
     * @return
     */
    String createChatApp(String uid, String name);

    /**
     * 聊天记录
     *
     * @param pageQuery
     * @return
     */
    PageResult<LogAppMessageDO> chatHistory(String conversationUid, Integer pageNo, Integer pageSize);

    /**
     * 查询会话
     *
     * @param scene
     * @param appUid
     */
    List<LogAppConversationRespVO> listConversation(String scene, String appUid);

    /**
     * 会话建议
     */
    List<String> chatSuggestion(String conversationUid);

    /**
     * 上传chatapp头像
     *
     * @param inputStream
     * @return
     */
    String updateAppAvatar(String appUid, InputStream inputStream);
}
