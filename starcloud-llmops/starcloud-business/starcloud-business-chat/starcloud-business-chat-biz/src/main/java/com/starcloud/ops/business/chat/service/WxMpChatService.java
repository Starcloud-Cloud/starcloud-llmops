package com.starcloud.ops.business.chat.service;

import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;

public interface WxMpChatService {

    /**
     * 解析url 并绑定wxmp聊天应用
     */
    void parseUrl(String url, Long mqUserId, String prompt);

    /**
     * 获取最近的聊天或新建聊天
     */
    String getRecentlyChatApp(String prompt);

    /**
     * 聊天并回复
     */
    void chatAndReply(ChatRequestVO chatRequestVO, Long mqUserId);

    /**
     * 发送消息
     */
    void sendMsg(Long mqUserId, String msg);


}
