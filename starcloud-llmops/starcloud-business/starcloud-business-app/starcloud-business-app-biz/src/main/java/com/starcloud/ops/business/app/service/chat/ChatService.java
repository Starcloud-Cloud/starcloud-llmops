package com.starcloud.ops.business.app.service.chat;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;
import com.starcloud.ops.business.log.api.conversation.vo.response.LogAppConversationRespVO;
import com.starcloud.ops.business.log.dal.dataobject.LogAppMessageDO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author starcloud
 */
public interface ChatService {


    /**
     * 对话聊天
     *
     * @param request
     */
    void chat(ChatRequestVO request);

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
     * @param
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
     * 查询app最新会话，当前用户
     *
     * @param appUid
     * @return
     */
    LogAppConversationRespVO getConversation(String appUid, String scene);

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
    String updateAppAvatar(String appUid, MultipartFile file) throws IOException;

    /**
     * 默认头像
     */
    List<String> defaultAvatar();
}
