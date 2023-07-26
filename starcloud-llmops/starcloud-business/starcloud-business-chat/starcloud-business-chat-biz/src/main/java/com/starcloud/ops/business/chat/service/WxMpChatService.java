package com.starcloud.ops.business.chat.service;

import com.starcloud.ops.business.app.controller.admin.chat.vo.ChatRequestVO;

public interface WxMpChatService {

    /**
     * 解析url 并绑定wxmp聊天应用
     */
    void parseUrl(String url, Long mqUserId);

    /**
     * 获取最近的聊天或新建聊天
     */
    String getRecentlyChatApp();

    /**
     * 聊天并回复
     */
    void chatAndReply(ChatRequestVO chatRequestVO, Long mqUserId);


}
